package com.example.bene.controller;

import com.example.bene.dto.*;
import com.example.bene.service.BeneDownloadService;
import com.example.bene.service.BeneService;
import com.example.bene.util.ServiceUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("bene")
public class BeneController {

    @Autowired
    private BeneService beneService;

    @Autowired
    private BeneDownloadService downloadService;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('MAKER')")
    public ResponseEntity<BeneSubmitResponse> Submit(@RequestBody Bene bene) throws SQLException, IOException {
        String referenceId=beneService.referenceId();
        bene.setReferenceId(referenceId);
        HttpServletRequest request= ServiceUtil.getServletRequest();
        String userCrn=ServiceUtil.getUserCrn(request);
        bene.setUserCrn(userCrn);
        BeneSubmitResponse response=beneService.insret(bene);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/detail")
    public ResponseEntity<Bene> Detail(@RequestBody Bene bene) throws SQLException {
            Bene bn = beneService.find(bene.getBeneNickName());
        return new ResponseEntity<>(bn,HttpStatus.OK);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('MAKER')")
    public ResponseEntity<BeneDeletedResponse> delete(@RequestBody DeleteRequest request) throws SQLException {
        HttpServletRequest req= ServiceUtil.getServletRequest();
        String server=req.getRemoteAddr();
        String userCrn=ServiceUtil.getUserCrn(req);
        request.setUserCrn(userCrn);
        BeneDeletedResponse response=beneService.delete(request,server);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<ListResponse> list(@RequestBody ListRequest request) throws SQLException {
        ListResponse bn =beneService.list(request);
        return new ResponseEntity<>(bn,HttpStatus.OK);
    }

    @PostMapping("/amend")
    public ResponseEntity<AmendBeneResponse> amend(@RequestBody Amend request) throws SQLException {
        AmendBeneResponse response=  beneService.amend(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/analytical")
    public ResponseEntity<List<Map<String,Object>>> analytical(@RequestBody AnalyticalRequest request) throws Exception {
        List<Map<String,Object>> response=  beneService.analytical(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/authorize")
    @PreAuthorize("hasRole('AUTHORIZER')")
    public ResponseEntity<AuthorizeResponse> authorize(@RequestBody AuthorizeRequest request) throws SQLException {
        AuthorizeResponse response = beneService.authorize(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/download")
    public ResponseEntity<DownloadResponse> analytical(@RequestBody DownloadRequest request) throws Exception {
        DownloadResponse response=downloadService.download(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
