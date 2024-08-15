package pl.teksusik.upmine.web;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

public abstract class CrudController<T, DTO> {
    protected final CrudService<T, DTO> service;

    public CrudController(CrudService<T, DTO> service) {
        this.service = service;
    }

    public void getAll(Context context) {
        context.status(HttpStatus.OK)
                .json(this.service.findAll());
    }

    public void getByUuid(Context context) {
        UUID uuid = parseUuidFromContext(context);
        if (uuid == null) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<T> entity = this.service.findByUuid(uuid);
        if (entity.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        context.status(HttpStatus.OK)
                .json(entity.get());
    }

    public void create(Context context) {
        DTO dto = context.bodyAsClass(getDtoClass());
        Optional<T> created = this.service.create(dto);
        if (created.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.CREATED)
                .json(created.get());
    }

    public void deleteByUuid(Context context) {
        UUID uuid = parseUuidFromContext(context);
        if (uuid == null) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<T> entity = this.service.findByUuid(uuid);
        if (entity.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        boolean success = this.service.deleteByUuid(uuid);
        context.status(success ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    public void update(Context context) {
        UUID uuid = parseUuidFromContext(context);
        if (uuid == null) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<T> entity = this.service.findByUuid(uuid);
        if (entity.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        DTO dto = context.bodyAsClass(getDtoClass());
        Optional<T> updated = this.service.update(uuid, dto);
        if (updated.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.OK)
                .json(updated.get());
    }

    private UUID parseUuidFromContext(Context context) {
        try {
            return UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    protected abstract Class<DTO> getDtoClass();
}
