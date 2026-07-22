package com.example.bene.job;

import com.example.bene.service.MigrationService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MigrationJob implements Job {


    @Autowired
    private MigrationService migrationService;

    public  static  final Logger log = LoggerFactory.getLogger(MigrationJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        try {
            JobDataMap dataMap = context.getMergedJobDataMap();
            Long id =dataMap.getLong("migrationId");
            log.info("Migration Started");
            migrationService.process(id);
            log.info("Migration Completed");
        }
        finally {
            try {
                context.getScheduler().deleteJob(context.getJobDetail().getKey());
            } catch (Exception e) {
                throw new JobExecutionException(e);
            }
        }
    }
}