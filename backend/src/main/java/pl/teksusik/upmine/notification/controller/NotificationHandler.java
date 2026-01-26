package pl.teksusik.upmine.notification.controller;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import pl.teksusik.upmine.notification.NotificationSettings;
import pl.teksusik.upmine.notification.dto.NotificationSettingsDto;

public class NotificationHandler {
    private final NotificationController controller;

    public NotificationHandler(NotificationController controller) {
        this.controller = controller;
    }

    @OpenApi(
            summary = "Get all notification settings",
            operationId = "getAllNotificationSettings",
            path = "/api/notificationSettings",
            methods = HttpMethod.GET,
            tags = {"Notification Settings"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = NotificationSettings[].class)}, description = "List of all notification settings")
            }
    )
    public void getAll(Context ctx) {
        this.controller.getAll(ctx);
    }

    @OpenApi(
            summary = "Get notification settings by UUID",
            operationId = "getNotificationSettingsByUuid",
            path = "/api/notificationSettings/{uuid}",
            methods = HttpMethod.GET,
            tags = {"Notification Settings"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Notification settings UUID", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = NotificationSettings.class)}, description = "Notification settings found"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format"),
                    @OpenApiResponse(status = "404", description = "Notification settings not found")
            }
    )
    public void getByUuid(Context ctx) {
        this.controller.getByUuid(ctx);
    }

    @OpenApi(
            summary = "Create notification settings",
            operationId = "createNotificationSettings",
            path = "/api/notificationSettings",
            methods = HttpMethod.POST,
            tags = {"Notification Settings"},
            requestBody = @OpenApiRequestBody(
                    content = {@OpenApiContent(from = NotificationSettingsDto.class)},
                    required = true,
                    description = "Notification settings data. For DISCORD type, discordWebhookUrl is required."
            ),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = NotificationSettings.class)}, description = "Notification settings created successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid request body or missing required fields")
            }
    )
    public void create(Context ctx) {
        this.controller.create(ctx);
    }

    @OpenApi(
            summary = "Update notification settings by UUID",
            operationId = "updateNotificationSettings",
            path = "/api/notificationSettings/{uuid}",
            methods = HttpMethod.PUT,
            tags = {"Notification Settings"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Notification settings UUID", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    content = {@OpenApiContent(from = NotificationSettingsDto.class)},
                    required = true,
                    description = "Updated notification settings data"
            ),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = NotificationSettings.class)}, description = "Notification settings updated successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format or request body"),
                    @OpenApiResponse(status = "404", description = "Notification settings not found")
            }
    )
    public void update(Context ctx) {
        this.controller.update(ctx);
    }

    @OpenApi(
            summary = "Delete notification settings by UUID",
            operationId = "deleteNotificationSettings",
            path = "/api/notificationSettings/{uuid}",
            methods = HttpMethod.DELETE,
            tags = {"Notification Settings"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "Notification settings UUID", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "204", description = "Notification settings deleted successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid UUID format or deletion failed"),
                    @OpenApiResponse(status = "404", description = "Notification settings not found")
            }
    )
    public void deleteByUuid(Context ctx) {
        this.controller.deleteByUuid(ctx);
    }
}
