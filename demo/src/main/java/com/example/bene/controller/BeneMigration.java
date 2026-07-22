package com.example.bene.controller;

import com.example.bene.dto.MigrationRequest;
import com.example.bene.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeneMigration {

    @Autowired
    private  MigrationService migrationService;

    @PostMapping(value = "migration/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submit (@ModelAttribute MigrationRequest request){
        migrationService.schedule(request);
        return new ResponseEntity<>("Migration is scheduled",HttpStatus.OK);
    }
}
