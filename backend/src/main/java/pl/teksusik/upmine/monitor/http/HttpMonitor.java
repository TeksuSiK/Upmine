package pl.teksusik.upmine.monitor.http;

import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class HttpMonitor extends Monitor {
    private String httpUrl;
    private List<Integer> httpAcceptedCodes;

    public HttpMonitor(UUID uuid, String name, MonitorType type, Instant creationDate, Duration checkInterval, String httpUrl, List<Integer> httpAcceptedCodes) {
        super(uuid, name, type, creationDate, checkInterval);
        this.httpUrl = httpUrl;
        this.httpAcceptedCodes = httpAcceptedCodes;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public List<Integer> getHttpAcceptedCodes() {
        return httpAcceptedCodes;
    }

    public void setHttpAcceptedCodes(List<Integer> httpAcceptedCodes) {
        this.httpAcceptedCodes = httpAcceptedCodes;
    }
}
