package pl.teksusik.upmine.notification.service;

import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.notification.NotificationSender;
import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.NotificationType;
import pl.teksusik.upmine.notification.discord.DiscordNotificationSettings;
import pl.teksusik.upmine.notification.dto.NotificationSettingsDto;
import pl.teksusik.upmine.storage.Repository;
import pl.teksusik.upmine.web.CrudService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NotificationService extends CrudService<NotificationSettings, NotificationSettingsDto> {
    private final Map<NotificationType, NotificationSender> notificationSenders = new HashMap<>();

    public NotificationService(Repository<NotificationSettings> repository) {
        super(repository);
    }

    @Override
    public Optional<NotificationSettings> create(NotificationSettingsDto notificationSettingsDto) {
        UUID uuid = UUID.randomUUID();
        String name = notificationSettingsDto.getName();
        NotificationType type = NotificationType.valueOf(notificationSettingsDto.getType());

        NotificationSettings notificationSettings;
        if (type == NotificationType.DISCORD) {
            String discordWebhookUrl = notificationSettingsDto.getDiscordWebhookUrl();
            notificationSettings = new DiscordNotificationSettings(uuid, name, type, discordWebhookUrl);
        } else {
            return Optional.empty();
        }

        NotificationSettings createdNotificationSettings = this.repository.save(notificationSettings);
        return Optional.ofNullable(createdNotificationSettings);
    }

    @Override
    public Optional<NotificationSettings> update(UUID uuid, NotificationSettingsDto notificationSettingsDto) {
        Optional<NotificationSettings> notificationSettingsOptional = this.repository.findByUuid(uuid);
        if (notificationSettingsOptional.isEmpty()) {
            return Optional.empty();
        }

        String newName = notificationSettingsDto.getName();
        String newType = notificationSettingsDto.getType();

        String newDiscordWebhookUrl = notificationSettingsDto.getDiscordWebhookUrl();

        NotificationSettings notificationSettings = notificationSettingsOptional.get();

        if (newName != null && !newName.equals(notificationSettings.getName())) {
            notificationSettings.setName(newName);
        }

        if (newType != null) {
            NotificationType notificationType;
            try {
                notificationType = NotificationType.valueOf(newType);
            } catch (IllegalArgumentException exception) {
                return Optional.empty();
            }

            if (!notificationType.equals(notificationSettings.getType())) {
                notificationSettings.setType(notificationType);
            }
        }

        if (notificationSettings.getType() == NotificationType.DISCORD && notificationSettings instanceof DiscordNotificationSettings discordNotificationSettings) {
            if (newDiscordWebhookUrl != null && !newDiscordWebhookUrl.equals(discordNotificationSettings.getDiscordWebhookUrl())) {
                discordNotificationSettings.setDiscordWebhookUrl(newDiscordWebhookUrl);
            }
        }

        NotificationSettings savedNotificationSettings = this.repository.save(notificationSettings);
        return Optional.ofNullable(savedNotificationSettings);
    }

    public void sendNotification(Monitor monitor, Heartbeat heartbeat) {
        for (NotificationSettings settings : monitor.getNotificationSettings()) {
            NotificationSender sender = this.notificationSenders.get(settings.getType());
            if (sender == null) {
                continue;
            }

            sender.sendNotification(monitor, heartbeat, settings);
        }
    }

    public void registerNotificationService(NotificationType notificationType, NotificationSender notificationSender) {
        this.notificationSenders.put(notificationType, notificationSender);
    }
}
