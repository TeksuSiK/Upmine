package pl.teksusik.upmine;

import pl.teksusik.upmine.heartbeat.repository.HeartbeatRepository;
import pl.teksusik.upmine.heartbeat.repository.SQLHeartbeatRepository;
import pl.teksusik.upmine.monitor.repository.MonitorRepository;
import pl.teksusik.upmine.monitor.repository.SQLMonitorRepository;
import pl.teksusik.upmine.monitor.service.MonitorService;
import pl.teksusik.upmine.storage.SQLStorage;

public class Upmine {
    private SQLStorage storage;

    private HeartbeatRepository heartbeatRepository;

    private MonitorRepository monitorRepository;
    private MonitorService monitorService;

    public void launch() {
        this.storage = new SQLStorage("jdbc:mariadb://127.0.0.1:3306/upmine?user=upmine&password=upmine");

        this.heartbeatRepository = new SQLHeartbeatRepository(this.storage);

        this.monitorRepository = new SQLMonitorRepository(this.storage, this.heartbeatRepository);
        this.monitorService = new MonitorService();
    }
}
