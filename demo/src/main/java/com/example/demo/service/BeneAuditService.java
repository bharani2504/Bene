package com.example.demo.service;

import com.example.demo.audit.BeneAudit;
import com.example.demo.model.Audit;
import com.example.demo.repo.BeneAuditRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BeneAuditService {

    private static BeneAuditRepo beneAuditRepo;

    public BeneAuditService(BeneAuditRepo beneAuditRepo){
        this.beneAuditRepo=beneAuditRepo;
    }

    public void createLog(Audit audit) {

        if(audit!=null){
            beneAuditRepo.save(audit);
        }
    }
}
