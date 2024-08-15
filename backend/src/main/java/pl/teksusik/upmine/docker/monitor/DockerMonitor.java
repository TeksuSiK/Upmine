package pl.teksusik.upmine.docker.monitor;

import pl.teksusik.upmine.docker.host.DockerHost;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.MonitorType;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class DockerMonitor extends Monitor {
    private DockerHost dockerHost;
    private String dockerContainerId;

    public DockerMonitor(UUID uuid, String name, MonitorType type, Instant creationDate, Duration checkInterval, DockerHost dockerHost, String dockerContainerId) {
        super(uuid, name, type, creationDate, checkInterval);
        this.dockerHost = dockerHost;
        this.dockerContainerId = dockerContainerId;
    }

    public DockerHost getDockerHost() {
        return dockerHost;
    }

    public void setDockerHost(DockerHost dockerHost) {
        this.dockerHost = dockerHost;
    }

    public String getDockerContainerId() {
        return dockerContainerId;
    }

    public void setDockerContainerId(String dockerContainerId) {
        this.dockerContainerId = dockerContainerId;
    }
}
