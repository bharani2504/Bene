package com.example.demo.controller;

import com.example.demo.dto.MigrationRequest;
import com.example.demo.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeneMigration {

    @Autowired
    private static MigrationService migrationService;

    @PostMapping("migration/submit")
    public ResponseEntity<String> submit (@RequestBody MigrationRequest request){
        migrationService.process(request);
        return new ResponseEntity<>("Migration is scheduled",HttpStatus.OK);
    }
}
