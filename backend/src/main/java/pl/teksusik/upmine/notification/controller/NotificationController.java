package pl.teksusik.upmine.notification.controller;

import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.dto.NotificationSettingsDto;
import pl.teksusik.upmine.web.CrudController;
import pl.teksusik.upmine.web.CrudService;

public class NotificationController extends CrudController<NotificationSettings, NotificationSettingsDto> {
    public NotificationController(CrudService<NotificationSettings, NotificationSettingsDto> service) {
        super(service);
    }

    @Override
    protected Class<NotificationSettingsDto> getDtoClass() {
        return NotificationSettingsDto.class;
    }
}
