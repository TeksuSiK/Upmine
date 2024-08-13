package pl.teksusik.upmine.monitor.service;

import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.availability.http.HttpAvailabilityChecker;
import pl.teksusik.upmine.availability.ping.PingAvailabilityChecker;
import pl.teksusik.upmine.availability.scheduler.AvailabilityCheckerScheduler;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;
import pl.teksusik.upmine.monitor.dto.MonitorDto;
import pl.teksusik.upmine.monitor.http.HttpMonitor;
import pl.teksusik.upmine.monitor.ping.PingMonitor;
import pl.teksusik.upmine.monitor.repository.MonitorRepository;
import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.service.NotificationService;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MonitorService {
    private final Map<MonitorType, AvailabilityChecker> availabilityCheckers = new HashMap<>(Map.of(
            MonitorType.HTTP, new HttpAvailabilityChecker(),
            MonitorType.PING, new PingAvailabilityChecker()
    ));

    private final NotificationService notificationService;
    private final MonitorRepository monitorRepository;
    private final AvailabilityCheckerScheduler availabilityCheckerScheduler;

    public MonitorService(NotificationService notificationService, MonitorRepository monitorRepository, AvailabilityCheckerScheduler availabilityCheckerScheduler) {
        this.notificationService = notificationService;
        this.monitorRepository = monitorRepository;
        this.availabilityCheckerScheduler = availabilityCheckerScheduler;
    }

    public long count() {
        return this.monitorRepository.count();
    }

    public Monitor save(Monitor monitor) {
        return this.monitorRepository.save(monitor);
    }

    public Optional<Monitor> findByUuid(UUID uuid) {
        return this.monitorRepository.findByUuid(uuid);
    }

    public List<Monitor> findAll() {
        return this.monitorRepository.findAll();
    }

    public boolean deleteByUuid(UUID uuid) {
        this.availabilityCheckerScheduler.deleteJob(uuid);
        return this.monitorRepository.deleteByUuid(uuid);
    }

    public Optional<Monitor> createMonitor(MonitorDto monitorDto) {
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
        } else {
            return Optional.empty();
        }

        Monitor createdMonitor = this.monitorRepository.save(monitor);
        this.availabilityCheckerScheduler.createJob(createdMonitor);
        return Optional.of(createdMonitor);
    }

    public Optional<Monitor> updateMonitor(UUID uuid, MonitorDto monitorDto) {
        Optional<Monitor> monitorOptional = this.monitorRepository.findByUuid(uuid);
        if (monitorOptional.isEmpty()) {
            return Optional.empty();
        }

        String newName = monitorDto.getName();
        String newType = monitorDto.getType();
        Long newCheckInterval = monitorDto.getCheckInterval();
        String newUrl = monitorDto.getHttpUrl();
        List<Integer> newAcceptedCodes = monitorDto.getHttpAcceptedCodes();
        String newAddress = monitorDto.getPingAddress();
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

        if (newNotificationSettings != null) {
            List<NotificationSettings> newNotificationSettingsList = newNotificationSettings.stream()
                    .map(UUID::fromString)
                    .map(this.notificationService::findByUuid)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            monitor.setNotificationSettings(newNotificationSettingsList);
        }

        Monitor savedMonitor = this.monitorRepository.save(monitor);
        this.availabilityCheckerScheduler.rescheduleJob(savedMonitor);
        return Optional.of(savedMonitor);
    }

    public Heartbeat checkAvailability(Monitor monitor) {
        AvailabilityChecker availabilityChecker = this.availabilityCheckers.get(monitor.getType());
        Heartbeat heartbeat = availabilityChecker.checkAvailability(monitor);
        monitor.addHeartbeat(heartbeat);

        this.monitorRepository.save(monitor);
        return heartbeat;
    }
}
