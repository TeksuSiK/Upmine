package pl.teksusik.upmine.monitor.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.dto.MonitorDto;
import pl.teksusik.upmine.monitor.service.MonitorService;

import java.util.Optional;
import java.util.UUID;

public class MonitorController {
    private final MonitorService monitorService;

    public MonitorController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    public void getAllMonitors(Context context) {
        context.status(HttpStatus.OK)
                .json(this.monitorService.findAll());
    }

    public void getMonitorByUuid(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<Monitor> monitor = this.monitorService.findByUuid(uuid);
        if (monitor.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        context.status(HttpStatus.OK)
                .json(monitor.get());
    }

    public void createMonitor(Context context) {
        MonitorDto monitorDto = context.bodyAsClass(MonitorDto.class);
        Optional<Monitor> createdMonitor = this.monitorService.createMonitor(monitorDto);
        if (createdMonitor.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.CREATED)
                .json(createdMonitor.get());
    }

    public void deleteMonitorByUuid(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<Monitor> monitor = this.monitorService.findByUuid(uuid);
        if (monitor.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        boolean success = this.monitorService.deleteByUuid(uuid);
        context.status(success ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    public void updateMonitor(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<Monitor> monitor = this.monitorService.findByUuid(uuid);
        if (monitor.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        MonitorDto monitorDto = context.bodyAsClass(MonitorDto.class);
        Optional<Monitor> updatedMonitor = this.monitorService.updateMonitor(uuid, monitorDto);
        if (updatedMonitor.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.OK)
                .json(updatedMonitor.get());
    }
}
