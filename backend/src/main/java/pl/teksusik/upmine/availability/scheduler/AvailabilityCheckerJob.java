package pl.teksusik.upmine.availability.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pl.teksusik.upmine.heartbeat.Heartbeat;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.service.MonitorService;

import java.util.UUID;

public class AvailabilityCheckerJob implements Job {
    private final MonitorService monitorService;

    public AvailabilityCheckerJob(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        UUID monitorUuid = UUID.fromString(jobExecutionContext.getJobDetail().getJobDataMap().getString("monitorUuid"));
        Monitor monitor = this.monitorService.findByUuid(monitorUuid)
                .orElseThrow(() -> new JobExecutionException("An error occurred while fetching monitor"));

        Heartbeat heartbeat = this.monitorService.checkAvailability(monitor);
    }
}
