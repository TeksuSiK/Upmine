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
import pl.teksusik.upmine.notification.controller.NotificationController;
import pl.teksusik.upmine.notification.repository.NotificationRepository;
import pl.teksusik.upmine.notification.repository.SQLNotificationRepository;
import pl.teksusik.upmine.notification.service.NotificationService;
import pl.teksusik.upmine.storage.SQLStorage;
import pl.teksusik.upmine.web.UpmineWebServer;

public class Upmine {
    private ApplicationConfiguration configuration;

    private SQLStorage storage;

    private HeartbeatRepository heartbeatRepository;

    private NotificationRepository notificationRepository;
    private NotificationService notificationService;
    private NotificationController notificationController;

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

        this.notificationRepository = new SQLNotificationRepository(this.storage);
        this.notificationRepository.createTablesIfNotExists();
        this.notificationService = new NotificationService(this.notificationRepository);
        this.notificationController = new NotificationController(this.notificationService);

        this.monitorRepository = new SQLMonitorRepository(this.storage, this.heartbeatRepository, this.notificationRepository);
        this.monitorRepository.createTablesIfNotExists();
        this.monitorService = new MonitorService(this.notificationService, this.monitorRepository, this.availabilityCheckerScheduler);
        this.monitorController = new MonitorController(this.monitorService);

        this.availabilityCheckerScheduler.setMonitorService(this.monitorService);
        this.availabilityCheckerScheduler.setNotificationService(this.notificationService);
        this.availabilityCheckerScheduler.startScheduler();
        this.availabilityCheckerScheduler.setupJobsForExistingMonitors();

        ApplicationConfiguration.WebConfiguration webConfiguration = this.configuration.getWebConfiguration();
        this.webServer = new UpmineWebServer(webConfiguration);
        this.webServer.launch()
                .get("/api/monitors", this.monitorController::getAllMonitors)
                .get("/api/monitors/{uuid}", this.monitorController::getMonitorByUuid)
                .post("/api/monitors", this.monitorController::createMonitor)
                .delete("/api/monitors/{uuid}", this.monitorController::deleteMonitorByUuid)
                .put("/api/monitors/{uuid}", this.monitorController::updateMonitor)
                .get("/api/notificationSettings", this.notificationController::getAllNotificationSettings)
                .get("/api/notificationSettings/{uuid}", this.notificationController::getNotificationSettingsByUuid)
                .post("/api/notificationSettings", this.notificationController::createNotificationSettings)
                .delete("/api/notificationSettings/{uuid}", this.notificationController::deleteNotificationSettings)
                .put("/api/notificationSettings/{uuid}", this.notificationController::updateNotificationSettings);
    }
}
