package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({
        "beneId",
        "beneName",
        "beneNicknName",
        "mobile",
        "email",
        "referenceId",
        "accounts"
})
public class Bene {

    @Id
    @GeneratedValue
    private long beneId;

    private String beneName;
    private String beneNicknName;
    private String mobile;
    private String email;
    private String referenceId;
    private List<Account> account;

}
