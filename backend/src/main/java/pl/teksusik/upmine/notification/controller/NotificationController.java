package pl.teksusik.upmine.notification.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.dto.NotificationSettingsDto;
import pl.teksusik.upmine.notification.service.NotificationService;

import java.util.Optional;
import java.util.UUID;

public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void getAllNotificationSettings(Context context) {
        context.status(HttpStatus.OK)
                .json(this.notificationService.findAll());
    }

    public void getNotificationSettingsByUuid(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<NotificationSettings> notificationSettings = this.notificationService.findByUuid(uuid);
        if (notificationSettings.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        context.status(HttpStatus.OK)
                .json(notificationSettings.get());
    }

    public void createNotificationSettings(Context context) {
        NotificationSettingsDto notificationSettingsDto = context.bodyAsClass(NotificationSettingsDto.class);
        Optional<NotificationSettings> createdNotificationSettings = this.notificationService.createNotificationSettings(notificationSettingsDto);
        if (createdNotificationSettings.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.CREATED)
                .json(createdNotificationSettings.get());
    }

    public void deleteNotificationSettings(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<NotificationSettings> notificationSettings = this.notificationService.findByUuid(uuid);
        if (notificationSettings.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        boolean success = this.notificationService.deleteByUuid(uuid);
        context.status(success ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    public void updateNotificationSettings(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<NotificationSettings> notificationSettings = this.notificationService.findByUuid(uuid);
        if (notificationSettings.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        NotificationSettingsDto dto = context.bodyAsClass(NotificationSettingsDto.class);
        Optional<NotificationSettings> updatedNotificationSettings = this.notificationService.updateNotificationSettings(uuid, dto);
        if (updatedNotificationSettings.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.OK)
                .json(updatedNotificationSettings.get());
    }
}
