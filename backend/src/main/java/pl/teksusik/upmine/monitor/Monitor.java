package pl.teksusik.upmine.monitor;

import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.notification.NotificationSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Monitor {
    private final UUID uuid;
    private String name;

    private MonitorType type;
    private Instant creationDate;
    private Duration checkInterval;

    private List<Heartbeat> heartbeats = new ArrayList<>();
    private List<NotificationSettings> notificationSettings = new ArrayList<>();

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

    public List<Heartbeat> getHeartbeats() {
        return heartbeats;
    }

    public void setHeartbeats(List<Heartbeat> heartbeats) {
        this.heartbeats = heartbeats;
    }

    public void addHeartbeat(Heartbeat heartbeat) {
        this.heartbeats.add(heartbeat);
    }

    public List<NotificationSettings> getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(List<NotificationSettings> notificationSettings) {
        this.notificationSettings = notificationSettings;
    }
}
