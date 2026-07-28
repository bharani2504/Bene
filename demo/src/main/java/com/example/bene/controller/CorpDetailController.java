package com.example.bene.controller;


import com.example.bene.entity.UserSession;
import com.example.bene.service.CorpCrnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorpDetailController {

    @Autowired
    private CorpCrnService corpCrnService;

    @PostMapping("/corp/save")
    public ResponseEntity<String> save(@RequestBody UserSession userSession){
        corpCrnService.save(userSession);
        return  new ResponseEntity<>("Corporate Onboarded Successfully", HttpStatus.ACCEPTED);
    }
}
