package com.example.demo.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ServiceRequest {

    private String beneNickname;
    private String refenceID;
    private Map<String,Object> context;
}
