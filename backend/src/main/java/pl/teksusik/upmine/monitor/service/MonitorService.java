package pl.teksusik.upmine.monitor.service;

import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.availability.scheduler.AvailabilityCheckerScheduler;
import pl.teksusik.upmine.docker.host.DockerHost;
import pl.teksusik.upmine.docker.host.service.DockerHostService;
import pl.teksusik.upmine.docker.monitor.DockerMonitor;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.http.HttpMonitor;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;
import pl.teksusik.upmine.monitor.dto.MonitorDto;
import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.service.NotificationService;
import pl.teksusik.upmine.ping.PingMonitor;
import pl.teksusik.upmine.storage.Repository;
import pl.teksusik.upmine.web.CrudService;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MonitorService extends CrudService<Monitor, MonitorDto> {
    private final Map<MonitorType, AvailabilityChecker> availabilityCheckers = new HashMap<>();

    private final DockerHostService dockerHostService;
    private final NotificationService notificationService;
    private final AvailabilityCheckerScheduler availabilityCheckerScheduler;

    public MonitorService(Repository<Monitor> repository, DockerHostService dockerHostService, NotificationService notificationService, AvailabilityCheckerScheduler availabilityCheckerScheduler) {
        super(repository);
        this.dockerHostService = dockerHostService;
        this.notificationService = notificationService;
        this.availabilityCheckerScheduler = availabilityCheckerScheduler;
    }

    @Override
    public boolean deleteByUuid(UUID uuid) {
        this.availabilityCheckerScheduler.deleteJob(uuid);
        return super.deleteByUuid(uuid);
    }

    @Override
    public Optional<Monitor> create(MonitorDto monitorDto) {
        UUID uuid = UUID.randomUUID();
        String name = monitorDto.getName();

        MonitorType type = MonitorType.valueOf(monitorDto.getType());
        Instant creationDate = Instant.now();
        Duration checkInterval = Duration.ofSeconds(monitorDto.getCheckInterval());

        Monitor monitor;
        if (type == MonitorType.HTTP) {
            String url = monitorDto.getHttpUrl();
            List<Integer> acceptedCodes = monitorDto.getHttpAcceptedCodes();

            monitor = new HttpMonitor(uuid, name, type, creationDate, checkInterval, url, acceptedCodes);
        } else if (type == MonitorType.PING) {
            String address = monitorDto.getPingAddress();

            monitor = new PingMonitor(uuid, name, type, creationDate, checkInterval, address);
        } else if (type == MonitorType.DOCKER) {
            UUID dockerHostUuid = UUID.fromString(monitorDto.getDockerHost());
            Optional<DockerHost> dockerHost = this.dockerHostService.findByUuid(dockerHostUuid);
            if (dockerHost.isEmpty()) {
                return Optional.empty();
            }
            String dockerContainerId = monitorDto.getDockerContainerId();

            monitor = new DockerMonitor(uuid, name, type, creationDate, checkInterval, dockerHost.get(), dockerContainerId);
        } else {
            return Optional.empty();
        }

        Monitor createdMonitor = this.repository.save(monitor);
        this.availabilityCheckerScheduler.createJob(createdMonitor);
        return Optional.of(createdMonitor);
    }

    @Override
    public Optional<Monitor> update(UUID uuid, MonitorDto monitorDto) {
        Optional<Monitor> monitorOptional = this.repository.findByUuid(uuid);
        if (monitorOptional.isEmpty()) {
            return Optional.empty();
        }

        String newName = monitorDto.getName();
        String newType = monitorDto.getType();
        Long newCheckInterval = monitorDto.getCheckInterval();
        String newUrl = monitorDto.getHttpUrl();
        List<Integer> newAcceptedCodes = monitorDto.getHttpAcceptedCodes();
        String newAddress = monitorDto.getPingAddress();
        String newDockerHost = monitorDto.getDockerHost();
        String newDockerContainerId = monitorDto.getDockerContainerId();
        List<String> newNotificationSettings = monitorDto.getNotificationSettings();

        Monitor monitor = monitorOptional.get();

        if (newName != null && !newName.equals(monitor.getName())) {
            monitor.setName(newName);
        }

        if (newType != null) {
            MonitorType monitorType;
            try {
                monitorType = MonitorType.valueOf(newType);
            } catch (IllegalArgumentException exception) {
                return Optional.empty();
            }

            if (!monitorType.equals(monitor.getType())) {
                monitor.setType(monitorType);
            }
        }

        if (newCheckInterval != null) {
            Duration checkInterval = Duration.ofSeconds(newCheckInterval);
            if (!checkInterval.equals(monitor.getCheckInterval())) {
                monitor.setCheckInterval(checkInterval);
            }
        }

        if (monitor.getType() == MonitorType.HTTP && monitor instanceof HttpMonitor httpMonitor) {
            if (newUrl != null && !newUrl.equals(httpMonitor.getHttpUrl())) {
                httpMonitor.setHttpUrl(newUrl);
            }

            if (newAcceptedCodes != null && !newAcceptedCodes.equals(httpMonitor.getHttpAcceptedCodes())) {
                httpMonitor.setHttpAcceptedCodes(newAcceptedCodes);
            }
        }

        if (monitor.getType() == MonitorType.PING && monitor instanceof PingMonitor pingMonitor) {
            if (newAddress != null && !newAddress.equals(pingMonitor.getPingAddress())) {
                pingMonitor.setPingAddress(newAddress);
            }
        }

        if (monitor.getType() == MonitorType.DOCKER && monitor instanceof DockerMonitor dockerMonitor) {
            if (newDockerHost != null && !newDockerHost.equals(dockerMonitor.getDockerHost().getUuid().toString())) {
                UUID dockerHostUuid = UUID.fromString(newDockerHost);
                Optional<DockerHost> dockerHost = this.dockerHostService.findByUuid(dockerHostUuid);
                if (dockerHost.isEmpty()) {
                    return Optional.empty();
                }
                dockerMonitor.setDockerHost(dockerHost.get());
            }

            if (newDockerContainerId != null && !newDockerContainerId.equals(dockerMonitor.getDockerContainerId())) {
                dockerMonitor.setDockerContainerId(newDockerContainerId);
            }
        }

        if (newNotificationSettings != null) {
            List<NotificationSettings> newNotificationSettingsList = newNotificationSettings.stream()
                    .map(UUID::fromString)
                    .map(this.notificationService::findByUuid)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            monitor.setNotificationSettings(newNotificationSettingsList);
        }

        Monitor savedMonitor = this.repository.save(monitor);
        this.availabilityCheckerScheduler.rescheduleJob(savedMonitor);
        return Optional.of(savedMonitor);
    }

    public Heartbeat checkAvailability(Monitor monitor) {
        AvailabilityChecker availabilityChecker = this.availabilityCheckers.get(monitor.getType());
        Heartbeat heartbeat = availabilityChecker.checkAvailability(monitor);
        monitor.addHeartbeat(heartbeat);

        this.repository.save(monitor);
        return heartbeat;
    }

    public void registerAvailabilityChecker(MonitorType monitorType, AvailabilityChecker availabilityChecker) {
        this.availabilityCheckers.put(monitorType, availabilityChecker);
    }
}
