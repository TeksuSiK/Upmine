package pl.teksusik.upmine.monitor.http;

import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class HttpMonitor extends Monitor {
    private String url;
    private List<Integer> acceptedCodes;

    public HttpMonitor(UUID uuid, String name, MonitorType type, Instant creationDate, Duration checkInterval, String url, List<Integer> acceptedCodes) {
        super(uuid, name, type, creationDate, checkInterval);
        this.url = url;
        this.acceptedCodes = acceptedCodes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Integer> getAcceptedCodes() {
        return acceptedCodes;
    }

    public void setAcceptedCodes(List<Integer> acceptedCodes) {
        this.acceptedCodes = acceptedCodes;
    }
}
