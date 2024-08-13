package pl.teksusik.upmine.notification.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.notification.NotificationSender;
import pl.teksusik.upmine.notification.NotificationSettings;

import java.io.IOException;

public class DiscordNotificationSender implements NotificationSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordNotificationSender.class);

    @Override
    public void sendNotification(Monitor monitor, Heartbeat heartbeat, NotificationSettings settings) {
        DiscordNotificationSettings discordNotificationSettings = (DiscordNotificationSettings) settings;

        DiscordWebhook webhook = new DiscordWebhook(discordNotificationSettings.getDiscordWebhookUrl());
        webhook.setContent(String.format("Monitor %s is now %s", monitor.getName(), heartbeat.getStatus()));
        try {
            webhook.execute();
        } catch (IOException exception) {
            LOGGER.error("An error occurred while sending notification", exception);
        }
    }
}
