package com.example.bene.controller;

import com.example.bene.dto.*;
import com.example.bene.service.BeneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("bene")
public class BeneController {

    @Autowired
    private BeneService beneService;

    @PostMapping("/submit")
    public ResponseEntity<BeneSubmitResponse> Submit(@RequestBody Bene bene) throws SQLException, IOException {
        String referenceId=beneService.referenceId();
        bene.setReferenceId(referenceId);
        BeneSubmitResponse response=beneService.insret(bene);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/detail")
    public ResponseEntity<Bene> Detail(@RequestBody Bene bene) throws SQLException {
            Bene bn = beneService.find(bene.getBeneNicknName());
        return new ResponseEntity<>(bn,HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<BeneDeletedResponse> delete(@RequestBody DeleteRequest request) throws SQLException {
        BeneDeletedResponse response=beneService.delete(request);
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
}
