package pl.teksusik.upmine.availability.scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.teksusik.upmine.monitor.Monitor;
import pl.teksusik.upmine.monitor.service.MonitorService;

import java.util.UUID;

public class AvailabilityCheckerScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityCheckerScheduler.class);

    private Scheduler scheduler;
    private MonitorService monitorService;

    public void startScheduler() {
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
            this.scheduler.setJobFactory(new AvailabilityCheckerJobFactory(this.monitorService));
            this.scheduler.start();
        } catch (SchedulerException exception) {
            LOGGER.error("An error occurred while starting the scheduler", exception);
        }
    }

    public void createJob(Monitor monitor) {
        JobDetail jobDetail = JobBuilder.newJob(AvailabilityCheckerJob.class)
                .withIdentity(monitor.getUuid().toString())
                .usingJobData("monitorUuid", monitor.getUuid().toString())
                .build();

        Trigger trigger = this.buildTrigger(monitor);

        try {
            this.scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException exception) {
            LOGGER.error("An error occurred while scheduling the job", exception);
        }
    }

    public void rescheduleJob(Monitor monitor) {
        Trigger trigger = this.buildTrigger(monitor);

        try {
            this.scheduler.rescheduleJob(trigger.getKey(), trigger);
        } catch (SchedulerException exception) {
            LOGGER.error("An error occurred while rescheduling the job", exception);
        }
    }

    public void deleteJob(UUID uuid) {
        try {
            this.scheduler.deleteJob(JobKey.jobKey(uuid.toString()));
        } catch (SchedulerException exception) {
            LOGGER.error("An error occurred while deleting the job", exception);
        }
    }

    private Trigger buildTrigger(Monitor monitor) {
        return TriggerBuilder.newTrigger()
                .withIdentity(monitor.getUuid().toString() + "-trigger")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(monitor.getCheckInterval().toMillis())
                        .repeatForever())
                .build();
    }

    public void setupJobsForExistingMonitors() {
        this.monitorService.findAll()
                .forEach(this::createJob);
    }

    public void setMonitorService(MonitorService monitorService) {
        this.monitorService = monitorService;
    }
}
