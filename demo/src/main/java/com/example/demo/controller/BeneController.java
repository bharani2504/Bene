package com.example.demo.controller;

import com.example.demo.model.Bene;
import com.example.demo.model.DeleteRequest;
import com.example.demo.model.ListRequest;
import com.example.demo.model.ListResponse;
import com.example.demo.service.BeneService;
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
    public ResponseEntity<String> Submit(@RequestBody Bene bene) throws SQLException, IOException {
        String referenceId=beneService.referenceId();
        bene.setReferenceId(referenceId);
        String response=beneService.insret(bene);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/detail")
    public ResponseEntity<Bene> Detail(@RequestBody Bene bene) throws SQLException {
            Bene bn = beneService.find(bene.getBeneNicknName());
        return new ResponseEntity<>(bn,HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody DeleteRequest request) throws SQLException {
         beneService.delete(request);
        return new ResponseEntity<>("Successfully deleted",HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity<ListResponse> list(@RequestBody ListRequest request) throws SQLException {
        ListResponse bn =beneService.list(request);
        return new ResponseEntity<>(bn,HttpStatus.OK);
    }

}
