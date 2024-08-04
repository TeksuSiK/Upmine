package pl.teksusik.upmine.monitor.service;

import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.availability.http.HttpAvailabilityChecker;
import pl.teksusik.upmine.availability.ping.PingAvailabilityChecker;
import pl.teksusik.upmine.availability.scheduler.AvailabilityCheckerScheduler;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;
import pl.teksusik.upmine.monitor.repository.MonitorRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//TODO Schedule new job at monitor creation, reschedule job at monitor update
public class MonitorService {
    private final Map<MonitorType, AvailabilityChecker> availabilityCheckers = new HashMap<>(Map.of(
            MonitorType.HTTP, new HttpAvailabilityChecker(),
            MonitorType.PING, new PingAvailabilityChecker()
    ));

    private final MonitorRepository monitorRepository;
    private final AvailabilityCheckerScheduler availabilityCheckerScheduler;

    public MonitorService(MonitorRepository monitorRepository, AvailabilityCheckerScheduler availabilityCheckerScheduler) {
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

    public Heartbeat checkAvailability(Monitor monitor) {
        AvailabilityChecker availabilityChecker = this.availabilityCheckers.get(monitor.getType());
        Heartbeat heartbeat = availabilityChecker.checkAvailability(monitor);
        monitor.addHeartbeat(heartbeat);

        this.monitorRepository.save(monitor);
        return heartbeat;
    }
}
