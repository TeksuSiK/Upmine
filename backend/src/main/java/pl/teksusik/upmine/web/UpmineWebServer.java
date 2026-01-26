package pl.teksusik.upmine.web;

import com.fasterxml.jackson.core.JsonParseException;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import pl.teksusik.upmine.configuration.ApplicationConfiguration;

public class UpmineWebServer {
    private Javalin javalin;

    private final ApplicationConfiguration.WebConfiguration webConfiguration;

    public UpmineWebServer(ApplicationConfiguration.WebConfiguration webConfiguration) {
        this.webConfiguration = webConfiguration;
    }

    public UpmineWebServer launch() {
        this.javalin = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
                pluginConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.withOpenApiInfo(info -> {
                        info.setTitle("Upmine API");
                        info.setVersion("1.0");
                        info.setDescription("Uptime monitoring API for tracking service availability");
                    });
                });
            }));
            config.registerPlugin(new SwaggerPlugin(swaggerConfig -> {
                swaggerConfig.setUiPath("/swagger");
            }));
        })
                .exception(JsonParseException.class, new JsonParseExceptionHandler()::handle)
                .start(this.webConfiguration.getHostname(), this.webConfiguration.getPort());
        return this;
    }

    public UpmineWebServer get(String endpoint, Handler handler) {
        this.javalin.get(endpoint, handler);
        return this;
    }

    public UpmineWebServer post(String endpoint, Handler handler) {
        this.javalin.post(endpoint, handler);
        return this;
    }

    public UpmineWebServer delete(String endpoint, Handler handler) {
        this.javalin.delete(endpoint, handler);
        return this;
    }

    public UpmineWebServer put(String endpoint, Handler handler) {
        this.javalin.put(endpoint, handler);
        return this;
    }

    public void stop() {
        this.javalin.stop();
    }
}
