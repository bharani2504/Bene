package com.example.bene.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BeneficiaryException.class)
    public ResponseEntity<Map<String,Object>>beneException(BeneficiaryException ex){
        Map<String,Object>body=new LinkedHashMap<>();
        body.put("time", Instant.now().toString());
        body.put("status","Error");
        body.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);


    }

}
