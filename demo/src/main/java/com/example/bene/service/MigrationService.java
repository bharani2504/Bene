package com.example.bene.service;

import com.example.bene.dto.Bene;
import com.example.bene.dto.MigrationRequest;
import com.example.bene.entity.Migration;
import com.example.bene.repo.BeneMigrationRepo;
import com.example.bene.repo.BeneRepo;
import com.example.bene.scheduler.MigrationScheduler;
import com.example.bene.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.util.List;

@Service
public class MigrationService {

    private  static Logger log = LoggerFactory.getLogger(MigrationService.class);

    @Autowired
    private  MigrationScheduler migrationScheduler;

    @Autowired
    private  BeneRepo beneRepo;

    @Autowired
    private BeneMigrationRepo beneMigrationRepo;


    public  void  schedule (MigrationRequest request){
        Migration migration = new Migration();

        if(request!=null) {
            String cron = null;
            if(request.getCron()!=null) {
                 cron = request.getCron();
                 migration.setCronExpression(cron);
            }

            if(request.getFile()!=null) {
                try {
                migration.setFileName(request.getFile().getName());
                migration.setExcelFile(request.getFile().getBytes());
                migration.setUploadedTime(new Date(System.currentTimeMillis()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("migration request => {}",migration);
            beneMigrationRepo.save(migration);
            migrationScheduler.scheduleMigration(migration.getId(), cron);
        }
        else{
            throw new RuntimeException("Invalid request");
        }
    }


    public void process(Long id)  {
        Migration migrate = new Migration();
        try {
            migrate.setId(id);
            migrate = beneMigrationRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Migration not found"));
             byte[]excel=migrate.getExcelFile();
             InputStream file = new ByteArrayInputStream(excel);

             Workbook workbook = WorkbookFactory.create(file);
             List<Bene> beneList= ExcelUtil.readExcel(workbook);
             migrate.setStatus("Completed");
             migrate.setTotalRecords(beneList.size());
             if (beneList != null && !beneList.isEmpty()) {
                 int success=0;
                 int failed=0;
                 for(Bene bene : beneList){
                    try {
                        beneRepo.insert(bene);
                        success++;
                    } catch (Exception e) {
                        failed++;
                        throw new RuntimeException(e);
                    }
                 }
                 log.info("Sucessfullly migrated =>{}",success,"records");
                 migrate.setSuccessRecords(success);


                 log.info("Failed migrated =>{}",failed,"records");
                 migrate.setFailedRecords(failed);
             }
             migrate.setCompletedTime(new Date(System.currentTimeMillis()));
             beneMigrationRepo.save(migrate);
        } catch (Exception e) {
            migrate.setStatus("failed");
            beneMigrationRepo.save(migrate);
            throw new RuntimeException(e);
        }
    }

}
