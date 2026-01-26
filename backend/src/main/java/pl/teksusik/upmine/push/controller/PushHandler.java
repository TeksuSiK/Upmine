package pl.teksusik.upmine.push.controller;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiResponse;
import pl.teksusik.upmine.heartbeat.Heartbeat;

public class PushHandler {
    private final PushController controller;

    public PushHandler(PushController controller) {
        this.controller = controller;
    }

    @OpenApi(
            summary = "Accept push availability notification",
            operationId = "acceptPush",
            path = "/api/push/{secret}",
            methods = HttpMethod.GET,
            tags = {"Push"},
            description = "Endpoint for PUSH monitors to report availability. Call this endpoint periodically from your service to indicate it is running.",
            pathParams = {
                    @OpenApiParam(name = "secret", description = "Push monitor secret key", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Heartbeat.class)}, description = "Push accepted, heartbeat recorded"),
                    @OpenApiResponse(status = "400", description = "Empty secret provided"),
                    @OpenApiResponse(status = "404", description = "Push monitor with given secret not found")
            }
    )
    public void acceptPush(Context ctx) {
        this.controller.acceptPush(ctx);
    }
}
