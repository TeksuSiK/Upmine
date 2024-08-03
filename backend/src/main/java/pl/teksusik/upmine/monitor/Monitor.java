package pl.teksusik.upmine.monitor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public abstract class Monitor {
    private final UUID uuid;
    private String name;

    private MonitorType type;
    private Instant creationDate;
    private Duration checkInterval;

    public Monitor(UUID uuid) {
        this.uuid = uuid;
    }

    public Monitor(UUID uuid, String name, MonitorType type, Instant creationDate, Duration checkInterval) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.creationDate = creationDate;
        this.checkInterval = checkInterval;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MonitorType getType() {
        return type;
    }

    public void setType(MonitorType type) {
        this.type = type;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Duration getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(Duration checkInterval) {
        this.checkInterval = checkInterval;
    }
}
