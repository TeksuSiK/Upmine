package pl.teksusik.upmine.notification;

import java.util.UUID;

public class NotificationSettings {
    private final UUID uuid;
    private String name;

    private NotificationType type;

    public NotificationSettings(UUID uuid) {
        this.uuid = uuid;
    }

    public NotificationSettings(UUID uuid, String name, NotificationType type) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
