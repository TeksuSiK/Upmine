package pl.teksusik.upmine.ping;

import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class PingMonitor extends Monitor {
    private String pingAddress;

    public PingMonitor(UUID uuid, String name, MonitorType type, Instant creationDate, Duration checkInterval, String pingAddress) {
        super(uuid, name, type, creationDate, checkInterval);
        this.pingAddress = pingAddress;
    }

    public String getPingAddress() {
        return pingAddress;
    }

    public void setPingAddress(String pingAddress) {
        this.pingAddress = pingAddress;
    }
}
