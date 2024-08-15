package pl.teksusik.upmine.push;

import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.heartbeat.HeartbeatFactory;
import pl.teksusik.upmine.monitor.Monitor;

import java.time.Duration;
import java.time.Instant;

public class PushAvailabilityChecker implements AvailabilityChecker {
    @Override
    public Heartbeat checkAvailability(Monitor monitor) {
        PushMonitor pushMonitor = (PushMonitor) monitor;

        if (pushMonitor.getHeartbeats().isEmpty()) {
            return HeartbeatFactory.notAvailable();
        }
        Heartbeat previousHeartbeat = pushMonitor.getHeartbeats().getFirst();
        if (previousHeartbeat == null) {
            return HeartbeatFactory.notAvailable();
        }

        Instant currentTime = Instant.now();
        Instant previousHeartbeatTime = previousHeartbeat.getCreationDate();
        Duration checkInterval = pushMonitor.getCheckInterval();

        Duration timeElapsed = Duration.between(previousHeartbeatTime, currentTime);
        if (timeElapsed.compareTo(checkInterval) > 0) {
            return HeartbeatFactory.notAvailable();
        } else {
            return HeartbeatFactory.available();
        }
    }
}
