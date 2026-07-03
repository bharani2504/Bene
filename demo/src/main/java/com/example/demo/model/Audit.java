package com.example.demo.model;

import lombok.Data;

@Data
public class Audit {

    private String serviceName;
    private String operationName;
    private String beneNickName;
    private String referenceId;
    private String context;
    private String status;
    private String responseString;
    private String data;
}
