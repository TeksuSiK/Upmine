package pl.teksusik.upmine.notification;

import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;

public interface NotificationSender {
    void sendNotification(Monitor monitor, Heartbeat heartbeat, NotificationSettings settings);
}
