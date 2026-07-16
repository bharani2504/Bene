package com.example.demo.job;

import com.example.demo.model.Amend;
import com.example.demo.model.Bene;
import com.example.demo.model.Filter;
import com.example.demo.model.ListRequest;
import com.example.demo.repo.BeneRepo;
import com.example.demo.service.BeneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BeneStatusUpdateJob {

    private static final Logger log = LoggerFactory.getLogger(BeneStatusUpdateJob.class);
    private  static   BeneRepo beneRepo;
    public BeneStatusUpdateJob(BeneRepo beneRepo){
        this.beneRepo=beneRepo;
    }

    @Scheduled(cron = "${bene.status.update.cron}")
    public static void updatePendingBene() throws SQLException {

        ListRequest request = new ListRequest();
        List<Filter> filters = new ArrayList<>();
        Filter filter = new Filter();
        filter.setName("status");
        filter.setValue("Pending");
        filters.add(filter);
        request.setFilters(filters);
        request.setFetchChild(true);
        List<Bene> bn = beneRepo.list(request);
        Date dt = new Date(System.currentTimeMillis());
        int updatedCount = 0;

        if (!bn.isEmpty()) {
            for (Bene bene : bn) {
                Amend amend = new Amend();
                BeanUtils.copyProperties(bene, amend);
                amend.setStatus("Approved");
                amend.setLastupdated(dt);
                beneRepo.amend(amend);
                updatedCount++;
            }
            log.info("BeneStatusUpdateJob completed, updated {} records", updatedCount);
        } else {
            log.info("no records to update");
        }
    }
}

