package com.example.bene.controller;

import com.example.bene.entity.UserSession;
import com.example.bene.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody UserSession session){
       Map<String,Object> mp=authService.login(session);
        return new ResponseEntity<>(mp, HttpStatus.OK);
    }

}
