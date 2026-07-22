package com.example.bene.job;

import com.example.bene.dto.Amend;
import com.example.bene.dto.Bene;
import com.example.bene.dto.Filter;
import com.example.bene.dto.ListRequest;
import com.example.bene.repo.BeneRepo;
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

