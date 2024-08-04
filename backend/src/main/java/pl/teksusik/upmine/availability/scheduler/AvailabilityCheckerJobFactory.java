package pl.teksusik.upmine.availability.scheduler;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import pl.teksusik.upmine.monitor.service.MonitorService;

public class AvailabilityCheckerJobFactory implements JobFactory {
    private final MonitorService monitorService;

    public AvailabilityCheckerJobFactory(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) {
        return new AvailabilityCheckerJob(this.monitorService);
    }
}
