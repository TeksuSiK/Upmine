package pl.teksusik.upmine.availability;

import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;

public interface AvailabilityChecker {
    Heartbeat checkAvailability(Monitor monitor);
}
