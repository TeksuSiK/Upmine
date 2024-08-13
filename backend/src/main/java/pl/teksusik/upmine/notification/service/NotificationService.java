package pl.teksusik.upmine.notification.service;

import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.notification.NotificationSender;
import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.NotificationType;
import pl.teksusik.upmine.notification.discord.DiscordNotificationSender;
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
}
