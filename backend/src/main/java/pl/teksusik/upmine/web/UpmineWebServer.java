package pl.teksusik.upmine.web;

import com.fasterxml.jackson.core.JsonParseException;
import io.javalin.Javalin;
import io.javalin.http.Handler;
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
