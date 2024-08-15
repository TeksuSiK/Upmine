package pl.teksusik.upmine.monitor.repository;

import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.push.PushMonitor;
import pl.teksusik.upmine.storage.Repository;

import java.util.Optional;

public interface MonitorRepository extends Repository<Monitor> {
    Optional<PushMonitor> findByPushSecret(String pushSecret);
}
