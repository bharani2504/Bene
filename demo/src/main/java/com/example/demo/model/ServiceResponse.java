package com.example.demo.model;

import lombok.Data;

import java.util.Map;

@Data
public class ServiceResponse {

    private String status;
    private String responseString;
    private Map<String,Object>data;
}
