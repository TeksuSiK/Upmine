package pl.teksusik.upmine.notification.service;

import pl.teksusik.upmine.notification.NotificationSender;
import pl.teksusik.upmine.notification.NotificationType;
import pl.teksusik.upmine.notification.repository.NotificationRepository;

import java.util.HashMap;
import java.util.Map;

public class NotificationService {
    private final Map<NotificationType, NotificationSender> notificationSenders = new HashMap<>(Map.of(

    ));

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
}
