package com.example.demo.service;

import com.example.demo.dto.MigrationRequest;
import com.example.demo.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MigrationService {


    public void process(MigrationRequest request)  {
        try {
            Workbook workbook = new XSSFWorkbook(request.getFile().getInputStream());
            ExcelUtil.readExcel(workbook);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
