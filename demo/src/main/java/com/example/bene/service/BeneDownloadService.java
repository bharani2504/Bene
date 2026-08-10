package com.example.bene.service;

import com.example.bene.dto.*;
import com.example.bene.repo.BeneRepo;
import com.example.bene.util.PdfDownload;
import com.example.bene.util.ServiceUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BeneDownloadService {

    private static BeneRepo benerepo;

    public BeneDownloadService(BeneRepo benerepo ){
        this.benerepo=benerepo;
    }

    public DownloadResponse download(DownloadRequest request) throws Exception {
        DownloadResponse response= new DownloadResponse();

        ListRequest req=request.getRequest();
        HttpServletRequest re= ServiceUtil.getServletRequest();
        String userCrn=ServiceUtil.getUserCrn(re);
        req.getFilters().add(new Filter("userCrn",userCrn));
        List<Bene> resp =benerepo.list(req);
        List<Map<String,Object>>res=new ArrayList<>();

        for (Bene bene:resp){
            Map<String,Object>ans=new HashMap<>();
            ans=ServiceUtil.convertPojoToMap(bene);
            res.add(ans);
        }

        HashMap<String,Object> params=new HashMap<>();
        params.put("datalist",res);
        params.put("columns",getColumns(request.getPageCode()));

        Map<String, Object> responseData = generateFile(request.getMimeType(), params);
        response = ServiceUtil.convertJsonToPojo(ServiceUtil.convertPojoToJson(responseData), DownloadResponse.class);
        response.setStatus("SUCCESS");

        return response;
    }

    private List<Map<String, String>> baseColumns() {
        return new ArrayList<>(List.of(
                col("SNO","SNO"),
                col("Crn", "userCrn"),
                col("A/C no", "accountNumber"),
                col("Initiation date", "createdDate"),
                col("Request details", "requestType")
        ));
    }


    private Map<String, String> col(String label, String name) {
        return Map.of(
               "label", label,
               "name", name
        );
    }

    private List<Map<String, String>> getColumns(String pageCode) {
        return columnMap.getOrDefault(pageCode, List.of());
    }

    private final Map<String, List<Map<String, String>>> columnMap = Map.of(
            "Pending", pendingColumns(),
            "Approved", approvedColumns(),
            "Rejected", rejectedColumns()
    );

    private List<Map<String, String>> pendingColumns() {
        List<Map<String, String>> cols = baseColumns();
        cols.add(col("PendingWith", "Authorizer"));
        return cols;
    }

    private List<Map<String, String>> rejectedColumns() {
        List<Map<String, String>> cols = baseColumns();
        cols.add(col("Rejection reason", "remarks"));
        return cols;
    }

    private List<Map<String, String>> approvedColumns() {
        List<Map<String, String>> cols = new ArrayList<>();
        cols.add(col("Payee Nickname", "beneNickName"));
        cols.add(col("Payee name", "beneName"));
        cols.add(col("Account details", "accountNumber"));
        cols.add(col("IFSC", "IFSC"));
        cols.add(col("Authorized Date", "lastupdated"));
        return cols;
    }


    private Map<String, Object> generateFile(String mimeType, HashMap<String, Object> params) {
        if ("application/vnd.ms-excel".equalsIgnoreCase(mimeType)) {
//            return BeneOpsExcelGenerator.buildExcelDocument(params);
        }

        if ("application/pdf".equalsIgnoreCase(mimeType)) {
            return PdfDownload.buildPdfDocument(params);
        }
        return Map.of();
    }
}
