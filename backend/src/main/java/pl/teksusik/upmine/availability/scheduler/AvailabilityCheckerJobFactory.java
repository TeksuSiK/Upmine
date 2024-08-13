package pl.teksusik.upmine.availability.scheduler;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import pl.teksusik.upmine.monitor.service.MonitorService;
import pl.teksusik.upmine.notification.service.NotificationService;

public class AvailabilityCheckerJobFactory implements JobFactory {
    private final MonitorService monitorService;
    private final NotificationService notificationService;

    public AvailabilityCheckerJobFactory(MonitorService monitorService, NotificationService notificationService) {
        this.monitorService = monitorService;
        this.notificationService = notificationService;
    }

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) {
        return new AvailabilityCheckerJob(this.monitorService, this.notificationService);
    }
}
