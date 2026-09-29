package com.example.bene.config;

import com.example.bene.job.BeneStatusUpdateJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class ScheduleConfig {

    @Value("${bene.status.update.cron}")
    private String cronExpression;

    @Autowired
    private Scheduler scheduler;
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
                .storeDurably()
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

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobDetail beneStatusUpdateJobDetail,Trigger beneStatusUpdateTrigger){
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        // Overwrite existing jobs
        factory.setOverwriteExistingJobs(true);
        factory.setJobDetails(beneStatusUpdateJobDetail);
        factory.setTriggers(beneStatusUpdateTrigger);
        return factory;
    }
}
