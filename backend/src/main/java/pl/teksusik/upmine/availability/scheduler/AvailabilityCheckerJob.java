package pl.teksusik.upmine.availability.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.service.MonitorService;
import pl.teksusik.upmine.notification.service.NotificationService;

import java.util.UUID;

public class AvailabilityCheckerJob implements Job {
    private final MonitorService monitorService;
    private final NotificationService notificationService;

    public AvailabilityCheckerJob(MonitorService monitorService, NotificationService notificationService) {
        this.monitorService = monitorService;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        UUID monitorUuid = UUID.fromString(jobExecutionContext.getJobDetail().getJobDataMap().getString("monitorUuid"));
        Monitor monitor = this.monitorService.findByUuid(monitorUuid)
                .orElseThrow(() -> new JobExecutionException("An error occurred while fetching monitor"));

        Heartbeat heartbeat = this.monitorService.checkAvailability(monitor);
        this.notificationService.sendNotification(monitor, heartbeat);
    }
}
