package pl.teksusik.upmine.notification.service;

import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.notification.NotificationSender;
import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.NotificationType;
import pl.teksusik.upmine.notification.discord.DiscordNotificationSender;
import pl.teksusik.upmine.notification.discord.DiscordNotificationSettings;
import pl.teksusik.upmine.notification.dto.NotificationSettingsDto;
import pl.teksusik.upmine.notification.repository.NotificationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NotificationService {
    private final Map<NotificationType, NotificationSender> notificationSenders = new HashMap<>(Map.of(
        NotificationType.DISCORD, new DiscordNotificationSender()
    ));

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public long count() {
        return this.notificationRepository.count();
    }

    public NotificationSettings save(NotificationSettings settings) {
        return this.notificationRepository.save(settings);
    }

    public Optional<NotificationSettings> findByUuid(UUID uuid) {
        return this.notificationRepository.findByUuid(uuid);
    }

    public List<NotificationSettings> findAll() {
        return this.notificationRepository.findAll();
    }

    public List<NotificationSettings> findByMonitorUuid(UUID monitorUuid) {
        return this.notificationRepository.findByMonitorUuid(monitorUuid);
    }

    public boolean deleteByUuid(UUID uuid) {
        return this.notificationRepository.deleteByUuid(uuid);
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

    public Optional<NotificationSettings> createNotificationSettings(NotificationSettingsDto notificationSettingsDto) {
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

        NotificationSettings createdNotificationSettings = this.notificationRepository.save(notificationSettings);
        return Optional.ofNullable(createdNotificationSettings);
    }

    public Optional<NotificationSettings> updateNotificationSettings(UUID uuid, NotificationSettingsDto notificationSettingsDto) {
        Optional<NotificationSettings> notificationSettingsOptional = this.notificationRepository.findByUuid(uuid);
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

        NotificationSettings savedNotificationSettings = this.notificationRepository.save(notificationSettings);
        return Optional.ofNullable(savedNotificationSettings);
    }
}
