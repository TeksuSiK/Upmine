package pl.teksusik.upmine.notification.dto;

public class NotificationSettingsDto {
    private String name;
    private String type;

    private String discordWebhookUrl;

    public NotificationSettingsDto() {
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }
}