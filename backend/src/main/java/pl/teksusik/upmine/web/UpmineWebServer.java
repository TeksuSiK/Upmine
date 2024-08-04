package pl.teksusik.upmine.web;

import com.fasterxml.jackson.core.JsonParseException;
import io.javalin.Javalin;
import pl.teksusik.upmine.configuration.ApplicationConfiguration;

public class UpmineWebServer {
    private Javalin javalin;

    private final ApplicationConfiguration.WebConfiguration webConfiguration;

    public UpmineWebServer(ApplicationConfiguration.WebConfiguration webConfiguration) {
        this.webConfiguration = webConfiguration;
    }

    public void launch() {
        this.javalin = Javalin.create()
                .exception(JsonParseException.class, new JsonParseExceptionHandler()::handle)
                .start(this.webConfiguration.getHostname(), this.webConfiguration.getPort());
    }

    public void stop() {
        this.javalin.stop();
    }
}
