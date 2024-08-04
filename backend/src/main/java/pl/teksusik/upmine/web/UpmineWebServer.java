package pl.teksusik.upmine.web;

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
                .start(this.webConfiguration.getHostname(), this.webConfiguration.getPort());
    }

    public void stop() {
        this.javalin.stop();
    }
}
