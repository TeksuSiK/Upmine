package pl.teksusik.upmine;

import pl.teksusik.upmine.availability.docker.DockerAvailabilityChecker;
import pl.teksusik.upmine.availability.http.HttpAvailabilityChecker;
import pl.teksusik.upmine.availability.ping.PingAvailabilityChecker;
import pl.teksusik.upmine.availability.scheduler.AvailabilityCheckerScheduler;
import pl.teksusik.upmine.configuration.ApplicationConfiguration;
import pl.teksusik.upmine.configuration.ConfigurationFactory;
import pl.teksusik.upmine.docker.controller.DockerHostController;
import pl.teksusik.upmine.docker.repository.DockerHostRepository;
import pl.teksusik.upmine.docker.repository.SQLDockerHostRepository;
import pl.teksusik.upmine.docker.service.DockerHostService;
import pl.teksusik.upmine.heartbeat.repository.HeartbeatRepository;
import pl.teksusik.upmine.heartbeat.repository.SQLHeartbeatRepository;
import pl.teksusik.upmine.monitor.MonitorType;
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

    private DockerHostRepository dockerHostRepository;
    private DockerHostService dockerHostService;
    private DockerHostController dockerHostController;

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

        this.dockerHostRepository = new SQLDockerHostRepository(this.storage);
        this.dockerHostRepository.createTablesIfNotExists();
        this.dockerHostService = new DockerHostService(this.dockerHostRepository);
        this.dockerHostService.registerExistingHosts();
        this.dockerHostController = new DockerHostController(this.dockerHostService);

        this.heartbeatRepository = new SQLHeartbeatRepository(this.storage);
        this.heartbeatRepository.createTablesIfNotExists();

        this.notificationRepository = new SQLNotificationRepository(this.storage);
        this.notificationRepository.createTablesIfNotExists();
        this.notificationService = new NotificationService(this.notificationRepository);
        this.notificationController = new NotificationController(this.notificationService);

        this.monitorRepository = new SQLMonitorRepository(this.storage, this.dockerHostRepository, this.heartbeatRepository, this.notificationRepository);
        this.monitorRepository.createTablesIfNotExists();
        this.monitorService = new MonitorService(this.dockerHostService, this.notificationService, this.monitorRepository, this.availabilityCheckerScheduler);
        this.monitorService.registerAvailabilityChecker(MonitorType.HTTP, new HttpAvailabilityChecker());
        this.monitorService.registerAvailabilityChecker(MonitorType.PING, new PingAvailabilityChecker());
        this.monitorService.registerAvailabilityChecker(MonitorType.DOCKER, new DockerAvailabilityChecker(this.dockerHostService));
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
                .put("/api/notificationSettings/{uuid}", this.notificationController::updateNotificationSettings)
                .get("/api/dockerHost", this.dockerHostController::getAllDockerHosts)
                .get("/api/dockerHost/{uuid}", this.dockerHostController::getDockerHostByUuid)
                .post("/api/dockerHost", this.dockerHostController::createDockerHost)
                .delete("/api/dockerHost/{uuid}", this.dockerHostController::deleteDockerHostByUuid)
                .put("/api/dockerHost/{uuid}", this.dockerHostController::updateDockerHost);
    }
}
