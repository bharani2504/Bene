package com.example.bene.config;

import com.example.bene.job.BeneStatusUpdateJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduleConfig {

    @Value("${bene.status.update.cron}")
    private String cronExpression;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("scheduled-task-");
        return scheduler;
    }


    @Bean
    public JobDetail beneStatusUpdateJobDetail(){
        return JobBuilder.newJob(BeneStatusUpdateJob.class)
                .withIdentity("beneStatusUpdateJob")
                .build();
    }

    @Bean
    public Trigger beneStatusUpdateTrigger(
            JobDetail beneStatusUpdateJobDetail) {

        return TriggerBuilder.newTrigger()
                .forJob(beneStatusUpdateJobDetail)
                .withIdentity("beneStatusUpdateTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }
}
