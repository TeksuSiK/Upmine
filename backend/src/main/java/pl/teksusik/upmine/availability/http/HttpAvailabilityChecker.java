package pl.teksusik.upmine.availability.http;

import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.heartbeat.Heartbeat;
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
import java.util.UUID;

public class HttpAvailabilityChecker implements AvailabilityChecker {

    @Override
    public Heartbeat checkAvailability(Monitor monitor) {
        HttpMonitor httpMonitor = (HttpMonitor) monitor;

        URI uri;
        try {
            uri = new URI(httpMonitor.getUrl());
        } catch (URISyntaxException exception) {
            Heartbeat heartbeat = new Heartbeat(UUID.randomUUID());
            heartbeat.setStatus(Status.NOT_AVAILABLE);
            heartbeat.setMessage(exception.getMessage());
            heartbeat.setCreationDate(Instant.now());
            return heartbeat;
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
            Heartbeat heartbeat = new Heartbeat(UUID.randomUUID());
            heartbeat.setStatus(Status.NOT_AVAILABLE);
            heartbeat.setMessage(exception.getMessage());
            heartbeat.setCreationDate(Instant.now());
            return heartbeat;
        }

        Heartbeat heartbeat = new Heartbeat(UUID.randomUUID());

        int responseCode = response.statusCode();
        heartbeat.setMessage(String.valueOf(responseCode));
        if (httpMonitor.getAcceptedCodes().contains(responseCode)) {
            heartbeat.setStatus(Status.AVAILABLE);
        } else {
            heartbeat.setStatus(Status.NOT_AVAILABLE);
        }

        heartbeat.setCreationDate(Instant.now());

        return heartbeat;
    }
}
