package pl.teksusik.upmine.monitor.service;

import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.availability.http.HttpAvailabilityChecker;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;

import java.util.HashMap;
import java.util.Map;

public class MonitorService {
    private final Map<MonitorType, AvailabilityChecker> availabilityCheckers = new HashMap<>(Map.of(
            MonitorType.HTTP, new HttpAvailabilityChecker()
    ));

    public Heartbeat checkAvailability(Monitor monitor) {
        AvailabilityChecker availabilityChecker = this.availabilityCheckers.get(monitor.getType());
        Heartbeat heartbeat = availabilityChecker.checkAvailability(monitor);

        return heartbeat;
    }
}
