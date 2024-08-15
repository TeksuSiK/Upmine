package pl.teksusik.upmine.monitor.dto;

import java.util.List;

public class MonitorDto {
    private String name;
    private String type;
    private Long checkInterval;
    private String httpUrl;
    private List<Integer> httpAcceptedCodes;
    private String pingAddress;
    private String dockerHost;
    private String dockerContainerId;
    private List<String> notificationSettings;

    public MonitorDto() {
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Long getCheckInterval() {
        return checkInterval;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public List<Integer> getHttpAcceptedCodes() {
        return httpAcceptedCodes;
    }

    public String getPingAddress() {
        return pingAddress;
    }

    public String getDockerHost() {
        return dockerHost;
    }

    public String getDockerContainerId() {
        return dockerContainerId;
    }

    public List<String> getNotificationSettings() {
        return notificationSettings;
    }
}
