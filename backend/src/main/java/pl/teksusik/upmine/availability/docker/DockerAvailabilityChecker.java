package pl.teksusik.upmine.availability.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.docker.service.DockerHostService;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.heartbeat.HeartbeatFactory;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.docker.DockerMonitor;

public class DockerAvailabilityChecker implements AvailabilityChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerAvailabilityChecker.class);

    private final DockerHostService dockerHostService;

    public DockerAvailabilityChecker(DockerHostService dockerHostService) {
        this.dockerHostService = dockerHostService;
    }

    @Override
    public Heartbeat checkAvailability(Monitor monitor) {
        DockerMonitor dockerMonitor = (DockerMonitor) monitor;

        DockerClient dockerClient = this.dockerHostService.getClient(dockerMonitor.getDockerHost());
        InspectContainerCmd inspectCommand = dockerClient.inspectContainerCmd(dockerMonitor.getDockerContainerId());
        InspectContainerResponse inspectResponse;
        try {
            inspectResponse = inspectCommand.exec();
        } catch (NotFoundException exception) {
            LOGGER.error("An error occurred while checking availability", exception);
            return HeartbeatFactory.notAvailable(exception.getMessage());
        }

        boolean running = Boolean.TRUE.equals(inspectResponse.getState().getRunning());
        if (running) {
            return HeartbeatFactory.available();
        } else {
            return HeartbeatFactory.notAvailable();
        }
    }
}
