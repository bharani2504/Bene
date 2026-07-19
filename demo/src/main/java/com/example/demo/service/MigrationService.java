package com.example.demo.service;

import com.example.demo.dto.Bene;
import com.example.demo.dto.MigrationRequest;
import com.example.demo.repo.BeneRepo;
import com.example.demo.scheduler.MigrationScheduler;
import com.example.demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class MigrationService {


    @Autowired
    private  MigrationScheduler migrationScheduler;

    @Autowired
    private  BeneRepo beneRepo;

    private static final String UPLOAD_DIR = "C:/Migration/uploads/";

    public  void  schedule (MigrationRequest request){

        if(request!=null) {
            String cron = null;
            if(request.getCron()!=null) {
                 cron = request.getCron();
            }
            String filePath = null;
            if(request.getFile()!=null) {

                try {
                    filePath = saveFile(request.getFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            migrationScheduler.scheduleMigration(filePath, cron);
        }
        else{
            throw new RuntimeException("Invalid request");
        }
    }


    public void process(String file)  {
        try {
            Workbook workbook = WorkbookFactory.create(new File(file));
             List<Bene> beneList= ExcelUtil.readExcel(workbook);
             if (beneList != null && !beneList.isEmpty()) {
                 for(Bene bene : beneList){
                     beneRepo.insert(bene);
                 }
             }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {

        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = UPLOAD_DIR + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }
}
