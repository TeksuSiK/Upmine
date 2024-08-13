package pl.teksusik.upmine.notification.repository;

import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.storage.Repository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends Repository<NotificationSettings> {
    List<NotificationSettings> findByMonitorUuid(UUID monitorUuid);
}
