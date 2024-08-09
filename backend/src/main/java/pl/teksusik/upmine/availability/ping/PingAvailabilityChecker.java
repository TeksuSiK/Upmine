package pl.teksusik.upmine.availability.ping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.availability.AvailabilityChecker;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.heartbeat.HeartbeatFactory;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.ping.PingMonitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingAvailabilityChecker implements AvailabilityChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(PingAvailabilityChecker.class);

    @Override
    public Heartbeat checkAvailability(Monitor monitor) {
        PingMonitor pingMonitor = (PingMonitor) monitor;

        InetAddress address;
        try {
            address = InetAddress.getByName(pingMonitor.getPingAddress());
        } catch (UnknownHostException exception) {
            LOGGER.error("An error occured while trying to resolve the IP address", exception);
            return HeartbeatFactory.notAvailable(exception.getMessage());
        }

        boolean reachable;
        try {
            reachable = address.isReachable(1000);
        } catch (IOException exception) {
            LOGGER.error("An error occured while trying to check the IP address", exception);
            return HeartbeatFactory.notAvailable(exception.getMessage());
        }

        if (reachable) {
            return HeartbeatFactory.available();
        } else {
            return HeartbeatFactory.notAvailable();
        }
    }
}
