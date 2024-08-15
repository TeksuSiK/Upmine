package pl.teksusik.upmine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.docker.monitor.DockerAvailabilityChecker;
import pl.teksusik.upmine.http.HttpAvailabilityChecker;
import pl.teksusik.upmine.ping.PingAvailabilityChecker;
import pl.teksusik.upmine.availability.scheduler.AvailabilityCheckerScheduler;
import pl.teksusik.upmine.configuration.ApplicationConfiguration;
import pl.teksusik.upmine.configuration.ConfigurationFactory;
import pl.teksusik.upmine.docker.host.controller.DockerHostController;
import pl.teksusik.upmine.docker.host.repository.DockerHostRepository;
import pl.teksusik.upmine.docker.host.repository.SQLDockerHostRepository;
import pl.teksusik.upmine.docker.host.service.DockerHostService;
import pl.teksusik.upmine.heartbeat.repository.HeartbeatRepository;
import pl.teksusik.upmine.heartbeat.repository.SQLHeartbeatRepository;
import pl.teksusik.upmine.monitor.MonitorType;
import pl.teksusik.upmine.monitor.controller.MonitorController;
import pl.teksusik.upmine.monitor.repository.MonitorRepository;
import pl.teksusik.upmine.monitor.repository.SQLMonitorRepository;
import pl.teksusik.upmine.monitor.service.MonitorService;
import pl.teksusik.upmine.notification.NotificationType;
import pl.teksusik.upmine.notification.controller.NotificationController;
import pl.teksusik.upmine.notification.discord.DiscordNotificationSender;
import pl.teksusik.upmine.notification.repository.NotificationRepository;
import pl.teksusik.upmine.notification.repository.SQLNotificationRepository;
import pl.teksusik.upmine.notification.service.NotificationService;
import pl.teksusik.upmine.push.PushAvailabilityChecker;
import pl.teksusik.upmine.push.controller.PushController;
import pl.teksusik.upmine.push.service.PushService;
import pl.teksusik.upmine.storage.SQLStorage;
import pl.teksusik.upmine.web.UpmineWebServer;

public class Upmine {
    private static final Logger LOGGER = LoggerFactory.getLogger(Upmine.class);

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

    private PushService pushService;
    private PushController pushController;

    private AvailabilityCheckerScheduler availabilityCheckerScheduler;

    private UpmineWebServer webServer;

    public void launch() {
        this.configuration = ConfigurationFactory.createConfiguration(ApplicationConfiguration.class);
        if (this.configuration == null) {
            LOGGER.error("An error occurred while loading configuration");
            System.exit(1);
            return;
        }

        ApplicationConfiguration.StorageConfiguration storageConfiguration = this.configuration.getStorageConfiguration();
        this.storage = new SQLStorage(storageConfiguration.getJdbcUrl());

        this.availabilityCheckerScheduler = new AvailabilityCheckerScheduler();

        this.dockerHostRepository = new SQLDockerHostRepository(this.storage);
        this.dockerHostRepository.createTablesIfNotExists();
        this.dockerHostService = new DockerHostService(this.dockerHostRepository);
        this.dockerHostService.registerExistingHosts();
        this.dockerHostController = new DockerHostController(this.dockerHostService);

        this.heartbeatRepository = new SQLHeartbeatRepository(this.storage);

        this.notificationRepository = new SQLNotificationRepository(this.storage);
        this.notificationRepository.createTablesIfNotExists();
        this.notificationService = new NotificationService(this.notificationRepository);
        this.notificationService.registerNotificationService(NotificationType.DISCORD, new DiscordNotificationSender());
        this.notificationController = new NotificationController(this.notificationService);

        this.monitorRepository = new SQLMonitorRepository(this.storage, this.dockerHostRepository, this.heartbeatRepository, this.notificationRepository);
        this.monitorRepository.createTablesIfNotExists();
        this.heartbeatRepository.createTablesIfNotExists();
        this.monitorService = new MonitorService(this.monitorRepository, this.dockerHostService, this.notificationService, this.availabilityCheckerScheduler);
        this.monitorService.registerAvailabilityChecker(MonitorType.HTTP, new HttpAvailabilityChecker());
        this.monitorService.registerAvailabilityChecker(MonitorType.PING, new PingAvailabilityChecker());
        this.monitorService.registerAvailabilityChecker(MonitorType.DOCKER, new DockerAvailabilityChecker(this.dockerHostService));
        this.monitorService.registerAvailabilityChecker(MonitorType.PUSH, new PushAvailabilityChecker());
        this.monitorController = new MonitorController(this.monitorService);

        this.pushService = new PushService(this.monitorService);
        this.pushController = new PushController(this.pushService);

        this.availabilityCheckerScheduler.setMonitorService(this.monitorService);
        this.availabilityCheckerScheduler.setNotificationService(this.notificationService);
        this.availabilityCheckerScheduler.startScheduler();
        this.availabilityCheckerScheduler.setupJobsForExistingMonitors();

        ApplicationConfiguration.WebConfiguration webConfiguration = this.configuration.getWebConfiguration();
        this.webServer = new UpmineWebServer(webConfiguration);
        this.webServer.launch()
                .get("/api/monitors", this.monitorController::getAll)
                .get("/api/monitors/{uuid}", this.monitorController::getByUuid)
                .post("/api/monitors", this.monitorController::create)
                .delete("/api/monitors/{uuid}", this.monitorController::deleteByUuid)
                .put("/api/monitors/{uuid}", this.monitorController::update)
                .get("/api/notificationSettings", this.notificationController::getAll)
                .get("/api/notificationSettings/{uuid}", this.notificationController::getByUuid)
                .post("/api/notificationSettings", this.notificationController::create)
                .delete("/api/notificationSettings/{uuid}", this.notificationController::deleteByUuid)
                .put("/api/notificationSettings/{uuid}", this.notificationController::update)
                .get("/api/dockerHost", this.dockerHostController::getAll)
                .get("/api/dockerHost/{uuid}", this.dockerHostController::getByUuid)
                .post("/api/dockerHost", this.dockerHostController::create)
                .delete("/api/dockerHost/{uuid}", this.dockerHostController::deleteByUuid)
                .put("/api/dockerHost/{uuid}", this.dockerHostController::update)
                .get("/api/push/{secret}", this.pushController::acceptPush);
    }
}
