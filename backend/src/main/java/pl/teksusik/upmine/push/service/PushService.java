package pl.teksusik.upmine.push.service;

import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.heartbeat.HeartbeatFactory;
import pl.teksusik.upmine.monitor.service.MonitorService;
import pl.teksusik.upmine.push.PushMonitor;

import java.util.Optional;

public class PushService {
    private final MonitorService monitorService;

    public PushService(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    public Optional<PushMonitor> findByPushSecret(String pushSecret) {
        return this.monitorService.findByPushSecret(pushSecret);
    }

    public Heartbeat addHeartbeat(PushMonitor pushMonitor) {
        Heartbeat availableHeartbeat = HeartbeatFactory.available();
        pushMonitor.addHeartbeat(availableHeartbeat);

        this.monitorService.save(pushMonitor);
        return availableHeartbeat;
    }
}
