package com.example.bene.controller;

import com.example.bene.entity.UserSession;
import com.example.bene.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(UserSession session){
        authService.login(session);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
