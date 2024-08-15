package pl.teksusik.upmine.push;

import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class PushMonitor extends Monitor {
    private final String pushSecret;

    public PushMonitor(UUID uuid, String name, MonitorType type, Instant creationDate, Duration checkInterval, String pushSecret) {
        super(uuid, name, type, creationDate, checkInterval);
        this.pushSecret = pushSecret;
    }

    public String getPushSecret() {
        return pushSecret;
    }
}
