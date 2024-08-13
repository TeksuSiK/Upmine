package pl.teksusik.upmine.notification.discord;

import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.NotificationType;

import java.util.UUID;

public class DiscordNotificationSettings extends NotificationSettings {
    private String discordWebhookUrl;

    public DiscordNotificationSettings(UUID uuid, String name, NotificationType type, String discordWebhookUrl) {
        super(uuid, name, type);
        this.discordWebhookUrl = discordWebhookUrl;
    }

    public String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }

    public void setDiscordWebhookUrl(String discordWebhookUrl) {
        this.discordWebhookUrl = discordWebhookUrl;
    }
}
