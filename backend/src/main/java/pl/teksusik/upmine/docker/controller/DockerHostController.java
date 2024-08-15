package pl.teksusik.upmine.docker.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import pl.teksusik.upmine.docker.DockerHost;
import pl.teksusik.upmine.docker.dto.DockerHostDto;
import pl.teksusik.upmine.docker.service.DockerHostService;

import java.util.Optional;
import java.util.UUID;

public class DockerHostController {
    private final DockerHostService dockerHostService;

    public DockerHostController(DockerHostService dockerHostService) {
        this.dockerHostService = dockerHostService;
    }
    
    public void getAllDockerHosts(Context context) {
        context.status(HttpStatus.OK)
                .json(this.dockerHostService.findAll());
    }
    
    public void getDockerHostByUuid(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<DockerHost> dockerHost = this.dockerHostService.findByUuid(uuid);
        if (dockerHost.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        context.status(HttpStatus.OK)
                .json(dockerHost.get());
    }

    public void createDockerHost(Context context) {
        DockerHostDto monitorDto = context.bodyAsClass(DockerHostDto.class);
        Optional<DockerHost> createdDockerHost = this.dockerHostService.createDockerHost(monitorDto);
        if (createdDockerHost.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.CREATED)
                .json(createdDockerHost.get());
    }

    public void deleteDockerHostByUuid(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<DockerHost> monitor = this.dockerHostService.findByUuid(uuid);
        if (monitor.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        boolean success = this.dockerHostService.deleteByUuid(uuid);
        context.status(success ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST);
    }

    public void updateDockerHost(Context context) {
        UUID uuid;
        try {
            uuid = UUID.fromString(context.pathParam("uuid"));
        } catch (IllegalArgumentException exception) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        Optional<DockerHost> monitor = this.dockerHostService.findByUuid(uuid);
        if (monitor.isEmpty()) {
            context.status(HttpStatus.NOT_FOUND);
            return;
        }

        DockerHostDto monitorDto = context.bodyAsClass(DockerHostDto.class);
        Optional<DockerHost> updatedDockerHost = this.dockerHostService.updateDockerHost(uuid, monitorDto);
        if (updatedDockerHost.isEmpty()) {
            context.status(HttpStatus.BAD_REQUEST);
            return;
        }

        context.status(HttpStatus.OK)
                .json(updatedDockerHost.get());
    }
}
