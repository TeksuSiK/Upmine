package pl.teksusik.upmine.monitor.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.dto.MonitorDto;

public class MonitorHandler {
    private final MonitorController controller;

    public MonitorHandler(MonitorController controller) {
        this.controller = controller;
    }

    @OpenApi(
            summary = "Get all monitors",
            operationId = "getAllMonitors",
            path = "/api/monitors",
            methods = HttpMethod.GET,
            tags = {"Monitors"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Monitor[].class)}, description = "List of all monitors")
            }
    )
    public void getAll(Context ctx) {
        this.controller.getAll(ctx);
    }

    @OpenApi(
            summary = "Get monitor by UUID",
            operationId = "getMonitorByUuid",
            path = "/api/monitors/{uuid}",
            methods = HttpMethod.GET,
            tags = {"Monitors"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Monitor UUID", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Monitor.class)}, description = "Monitor found"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format"),
                    @OpenApiResponse(status = "404", description = "Monitor not found")
            }
    )
    public void getByUuid(Context ctx) {
        this.controller.getByUuid(ctx);
    }

    @OpenApi(
            summary = "Create a new monitor",
            operationId = "createMonitor",
            path = "/api/monitors",
            methods = HttpMethod.POST,
            tags = {"Monitors"},
            requestBody = @OpenApiRequestBody(
                    content = {@OpenApiContent(from = MonitorDto.class)},
                    required = true,
                    description = "Monitor data. Fields required depend on monitor type: HTTP requires httpUrl and httpAcceptedCodes, PING requires pingAddress, DOCKER requires dockerHost and dockerContainerId, PUSH has no additional required fields."
            ),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = Monitor.class)}, description = "Monitor created successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid request body or missing required fields")
            }
    )
    public void create(Context ctx) {
        this.controller.create(ctx);
    }

    @OpenApi(
            summary = "Update monitor by UUID",
            operationId = "updateMonitor",
            path = "/api/monitors/{uuid}",
            methods = HttpMethod.PUT,
            tags = {"Monitors"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Monitor UUID", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    content = {@OpenApiContent(from = MonitorDto.class)},
                    required = true,
                    description = "Updated monitor data"
            ),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Monitor.class)}, description = "Monitor updated successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format or request body"),
                    @OpenApiResponse(status = "404", description = "Monitor not found")
            }
    )
    public void update(Context ctx) {
        this.controller.update(ctx);
    }

    @OpenApi(
            summary = "Delete monitor by UUID",
            operationId = "deleteMonitor",
            path = "/api/monitors/{uuid}",
            methods = HttpMethod.DELETE,
            tags = {"Monitors"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Monitor UUID", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "204", description = "Monitor deleted successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format or deletion failed"),
                    @OpenApiResponse(status = "404", description = "Monitor not found")
            }
    )
    public void deleteByUuid(Context ctx) {
        this.controller.deleteByUuid(ctx);
    }
}
