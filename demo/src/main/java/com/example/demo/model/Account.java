package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Account {

    @Id
    private Long id;
    private long beneId;
    private String accountName;
    private String accountNumber;
    private String IFSC;
    private Double amount;
    private String bank;

}
