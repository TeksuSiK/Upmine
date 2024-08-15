package pl.teksusik.upmine.docker.host.controller;

import pl.teksusik.upmine.docker.host.DockerHost;
import pl.teksusik.upmine.docker.host.dto.DockerHostDto;
import pl.teksusik.upmine.web.CrudController;
import pl.teksusik.upmine.web.CrudService;

public class DockerHostController extends CrudController<DockerHost, DockerHostDto> {
    public DockerHostController(CrudService<DockerHost, DockerHostDto> service) {
        super(service);
    }

    @Override
    protected Class<DockerHostDto> getDtoClass() {
        return DockerHostDto.class;
    }
}
