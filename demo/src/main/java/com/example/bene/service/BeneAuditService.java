package com.example.bene.service;

import com.example.bene.entity.Audit;
import com.example.bene.repo.BeneAuditRepo;
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
