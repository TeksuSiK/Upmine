package pl.teksusik.upmine.docker.host.controller;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import pl.teksusik.upmine.docker.host.DockerHost;
import pl.teksusik.upmine.docker.host.dto.DockerHostDto;

public class DockerHostHandler {
    private final DockerHostController controller;

    public DockerHostHandler(DockerHostController controller) {
        this.controller = controller;
    }

    @OpenApi(
            summary = "Get all Docker hosts",
            operationId = "getAllDockerHosts",
            path = "/api/dockerHost",
            methods = HttpMethod.GET,
            tags = {"Docker Hosts"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DockerHost[].class)}, description = "List of all registered Docker hosts")
            }
    )
    public void getAll(Context ctx) {
        this.controller.getAll(ctx);
    }

    @OpenApi(
            summary = "Get Docker host by UUID",
            operationId = "getDockerHostByUuid",
            path = "/api/dockerHost/{uuid}",
            methods = HttpMethod.GET,
            tags = {"Docker Hosts"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Docker host UUID", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DockerHost.class)}, description = "Docker host found"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format"),
                    @OpenApiResponse(status = "404", description = "Docker host not found")
            }
    )
    public void getByUuid(Context ctx) {
        this.controller.getByUuid(ctx);
    }

    @OpenApi(
            summary = "Register a new Docker host",
            operationId = "createDockerHost",
            path = "/api/dockerHost",
            methods = HttpMethod.POST,
            tags = {"Docker Hosts"},
            requestBody = @OpenApiRequestBody(
                    content = {@OpenApiContent(from = DockerHostDto.class)},
                    required = true,
                    description = "Docker host data. Address should be in format tcp://hostname:port (e.g., tcp://localhost:2375)"
            ),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = DockerHost.class)}, description = "Docker host registered successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid request body, missing required fields, or invalid Docker host address")
            }
    )
    public void create(Context ctx) {
        this.controller.create(ctx);
    }

    @OpenApi(
            summary = "Update Docker host by UUID",
            operationId = "updateDockerHost",
            path = "/api/dockerHost/{uuid}",
            methods = HttpMethod.PUT,
            tags = {"Docker Hosts"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Docker host UUID", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    content = {@OpenApiContent(from = DockerHostDto.class)},
                    required = true,
                    description = "Updated Docker host data"
            ),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DockerHost.class)}, description = "Docker host updated successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format or request body"),
                    @OpenApiResponse(status = "404", description = "Docker host not found")
            }
    )
    public void update(Context ctx) {
        this.controller.update(ctx);
    }

    @OpenApi(
            summary = "Delete Docker host by UUID",
            operationId = "deleteDockerHost",
            path = "/api/dockerHost/{uuid}",
            methods = HttpMethod.DELETE,
            tags = {"Docker Hosts"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Docker host UUID", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "204", description = "Docker host deleted successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format or deletion failed"),
                    @OpenApiResponse(status = "404", description = "Docker host not found")
            }
    )
    public void deleteByUuid(Context ctx) {
        this.controller.deleteByUuid(ctx);
    }
}
