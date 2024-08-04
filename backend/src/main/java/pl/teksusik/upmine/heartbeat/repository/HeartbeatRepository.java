package pl.teksusik.upmine.heartbeat.repository;

import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.storage.Repository;

import java.util.List;
import java.util.UUID;

public interface HeartbeatRepository extends Repository<Heartbeat> {
    List<Heartbeat> findByMonitorUuid(UUID monitorUuid);
    List<Heartbeat> findByMonitor(Monitor monitor);
}
