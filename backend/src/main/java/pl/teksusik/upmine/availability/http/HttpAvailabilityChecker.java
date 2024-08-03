package pl.teksusik.upmine.availability.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.heartbeat.HeartbeatFactory;
import pl.teksusik.upmine.heartbeat.Status;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.http.HttpMonitor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

public class HttpAvailabilityChecker implements AvailabilityChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpAvailabilityChecker.class);

    @Override
    public Heartbeat checkAvailability(Monitor monitor) {
        HttpMonitor httpMonitor = (HttpMonitor) monitor;

        URI uri;
        try {
            uri = new URI(httpMonitor.getUrl());
        } catch (URISyntaxException exception) {
            LOGGER.error("An error occurred while getting the URI", exception);
            return HeartbeatFactory.notAvailable(exception.getMessage());
        }

        HttpClient client = HttpClient.newBuilder()
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .HEAD()
                .build();

        HttpResponse<Void> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException exception) {
            LOGGER.error("An error occurred while sending the request", exception);
            return HeartbeatFactory.notAvailable(exception.getMessage());
        } finally {
            client.close();
        }

        Heartbeat heartbeat = HeartbeatFactory.undefined();

        int responseCode = response.statusCode();
        if (httpMonitor.getAcceptedCodes().contains(responseCode)) {
            heartbeat.setStatus(Status.AVAILABLE);
        } else {
            heartbeat.setStatus(Status.NOT_AVAILABLE);
        }

        heartbeat.setMessage(String.valueOf(responseCode));
        heartbeat.setCreationDate(Instant.now());

        return heartbeat;
    }
}
