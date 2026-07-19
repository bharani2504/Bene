package com.example.demo.util;

import com.example.demo.dto.Account;
import com.example.demo.dto.Bene;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    public static List<Bene> readExcel(Workbook workbook){

        Sheet beneSheet = workbook.getSheet("Beneficiary");
        Sheet accSheet=workbook.getSheet("Account");


        Map<Long,Bene> beneMap=new LinkedHashMap<>();
        List<Bene> beneList=new ArrayList<>();
         for(Row row: beneSheet){
             Bene bene= new Bene();
           if (row.getRowNum() == 0) {
               continue;
           }
           bene.setBeneId((long) row.getCell(0).getNumericCellValue());
           bene.setBeneName(row.getCell(1).getStringCellValue());
           bene.setBeneNicknName(row.getCell(2).getStringCellValue());
           bene.setEmail(row.getCell(3).getStringCellValue());
           bene.setMobile(row.getCell(4).getStringCellValue());
           bene.setStatus("Approved");
           bene.setMigrationStatus("M");
           beneMap.put(bene.getBeneId(),bene);
       }

        for (Row row : accSheet) {
            if (row.getRowNum() == 0) continue;

            Long beneId = (long) row.getCell(0).getNumericCellValue();
            List<Account> accounts= new ArrayList<>();
            Account act = new Account();
            act.setBeneId(beneId);
            act.setBank(row.getCell(1).getStringCellValue());
            act.setAmount(row.getCell(2).getNumericCellValue());
            act.setAccountNumber(row.getCell(3).getStringCellValue());
            act.setIFSC(row.getCell(4).getStringCellValue());
            act.setAccountName(row.getCell(5).getStringCellValue());
            accounts.add(act);
            Bene bn = beneMap.get(beneId);
            if (bn != null) {
                bn.setAccount(accounts);
                beneList.add(bn);
            }
        }

        return beneList;
    }
}
