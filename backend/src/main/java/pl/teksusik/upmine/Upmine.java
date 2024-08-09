package pl.teksusik.upmine;

import pl.teksusik.upmine.availability.scheduler.AvailabilityCheckerScheduler;
import pl.teksusik.upmine.configuration.ApplicationConfiguration;
import pl.teksusik.upmine.configuration.ConfigurationFactory;
import pl.teksusik.upmine.heartbeat.repository.HeartbeatRepository;
import pl.teksusik.upmine.heartbeat.repository.SQLHeartbeatRepository;
import pl.teksusik.upmine.monitor.controller.MonitorController;
import pl.teksusik.upmine.monitor.repository.MonitorRepository;
import pl.teksusik.upmine.monitor.repository.SQLMonitorRepository;
import pl.teksusik.upmine.monitor.service.MonitorService;
import pl.teksusik.upmine.storage.SQLStorage;
import pl.teksusik.upmine.web.UpmineWebServer;

public class Upmine {
    private ApplicationConfiguration configuration;

    private SQLStorage storage;

    private HeartbeatRepository heartbeatRepository;

    private MonitorRepository monitorRepository;
    private MonitorService monitorService;
    private MonitorController monitorController;

    private AvailabilityCheckerScheduler availabilityCheckerScheduler;

    private UpmineWebServer webServer;

    public void launch() {
        this.configuration = ConfigurationFactory.createConfiguration(ApplicationConfiguration.class);

        ApplicationConfiguration.StorageConfiguration storageConfiguration = this.configuration.getStorageConfiguration();
        this.storage = new SQLStorage(storageConfiguration.getJdbcUrl());

        this.availabilityCheckerScheduler = new AvailabilityCheckerScheduler();

        this.heartbeatRepository = new SQLHeartbeatRepository(this.storage);
        this.heartbeatRepository.createTablesIfNotExists();

        this.monitorRepository = new SQLMonitorRepository(this.storage, this.heartbeatRepository);
        this.monitorRepository.createTablesIfNotExists();
        this.monitorService = new MonitorService(this.monitorRepository, this.availabilityCheckerScheduler);
        this.monitorController = new MonitorController(this.monitorService);

        this.availabilityCheckerScheduler.setMonitorService(monitorService);
        this.availabilityCheckerScheduler.startScheduler();
        this.availabilityCheckerScheduler.setupJobsForExistingMonitors();

        ApplicationConfiguration.WebConfiguration webConfiguration = this.configuration.getWebConfiguration();
        this.webServer = new UpmineWebServer(webConfiguration);
        this.webServer.launch();
        this.webServer.get("/api/monitors", this.monitorController::getAllMonitors)
                .get("/api/monitors/{uuid}", this.monitorController::getMonitorByUuid)
                .post("/api/monitors", this.monitorController::createMonitor)
                .delete("/api/monitors/{uuid}", this.monitorController::deleteMonitorByUuid)
                .put("/api/monitors/{uuid}", this.monitorController::updateMonitor);
    }
}
