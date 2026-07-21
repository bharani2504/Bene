package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name ="BENE_INTEGRATION_AUDIT")
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long audit_id;

    @Column(name = "serviceName")
    private String serviceName;

    @Column(name = "operationName")
    private String operationName;

    @Column(name = "beneNickName")
    private String beneNickName;

    @Column(name = "referenceId")
    private String referenceId;

    @Column(name = "context")
    private String context;

    @Column(name = "status")
    private String status;

    @Column(name = "responseString")
    private String responseString;

    @Column(name = "data")
    private String data;

    @Column(name = "service_url")
    private String serviceUrl;

}
