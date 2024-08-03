package pl.teksusik.upmine.heartbeat;

import java.time.Instant;
import java.util.UUID;

public class HeartbeatFactory {
    public static Heartbeat notAvailable(String message) {
        return new Heartbeat(UUID.randomUUID(), Status.NOT_AVAILABLE, message, Instant.now());
    }

    public static Heartbeat notAvailable() {
        return notAvailable("");
    }

    public static Heartbeat available(String message) {
        return new Heartbeat(UUID.randomUUID(), Status.AVAILABLE, message, Instant.now());
    }

    public static Heartbeat available() {
        return available("");
    }

    public static Heartbeat undefined() {
        return new Heartbeat(UUID.randomUUID(), Instant.now());
    }
}
