package pl.teksusik.upmine.monitor.ping;

import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class PingMonitor extends Monitor {
    private String address;

    public PingMonitor(UUID uuid, String name, MonitorType type, Instant creationDate, Duration checkInterval, String address) {
        super(uuid, name, type, creationDate, checkInterval);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
