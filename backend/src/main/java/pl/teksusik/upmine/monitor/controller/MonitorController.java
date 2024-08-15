package pl.teksusik.upmine.monitor.controller;

import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.dto.MonitorDto;
import pl.teksusik.upmine.web.CrudController;
import pl.teksusik.upmine.web.CrudService;

public class MonitorController extends CrudController<Monitor, MonitorDto> {
    public MonitorController(CrudService<Monitor, MonitorDto> service) {
        super(service);
    }

    @Override
    protected Class<MonitorDto> getDtoClass() {
        return MonitorDto.class;
    }
}
