package pl.teksusik.upmine;

import pl.teksusik.upmine.configuration.ApplicationConfiguration;
import pl.teksusik.upmine.configuration.ConfigurationFactory;
import pl.teksusik.upmine.heartbeat.repository.HeartbeatRepository;
import pl.teksusik.upmine.heartbeat.repository.SQLHeartbeatRepository;
import pl.teksusik.upmine.monitor.repository.MonitorRepository;
import pl.teksusik.upmine.monitor.repository.SQLMonitorRepository;
import pl.teksusik.upmine.monitor.service.MonitorService;
import pl.teksusik.upmine.storage.SQLStorage;

public class Upmine {
    private ApplicationConfiguration configuration;

    private SQLStorage storage;

    private HeartbeatRepository heartbeatRepository;

    private MonitorRepository monitorRepository;
    private MonitorService monitorService;

    public void launch() {
        this.configuration = ConfigurationFactory.createConfiguration(ApplicationConfiguration.class);

        ApplicationConfiguration.StorageConfiguration storageConfiguration = this.configuration.getStorageConfiguration();
        this.storage = new SQLStorage(storageConfiguration.getJdbcUrl());

        this.heartbeatRepository = new SQLHeartbeatRepository(this.storage);

        this.monitorRepository = new SQLMonitorRepository(this.storage, this.heartbeatRepository);
        this.monitorService = new MonitorService();
    }
}
