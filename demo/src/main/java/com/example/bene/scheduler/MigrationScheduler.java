package com.example.bene.scheduler;

import com.example.bene.job.MigrationJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MigrationScheduler {

    @Autowired
    private Scheduler scheduler;

    public void scheduleMigration(Long id ,String cron)  {

        try {
            JobDetail jobDetail = JobBuilder.newJob(MigrationJob.class)
                    .withIdentity(UUID.randomUUID().toString())
                    .usingJobData("migrationId", id)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(UUID.randomUUID().toString())
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
