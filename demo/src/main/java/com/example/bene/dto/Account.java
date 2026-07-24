package com.example.bene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Date;

@Data
@JsonPropertyOrder({
        "id",
        "beneId",
        "accountName",
        "accountNumber",
        "IFSC",
        "amount",
        "bank"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    @Id
    private Long id;
    private long beneId;
    private String accountName;
    private String accountNumber;
    private String IFSC;
    private Double amount;
    private String bank;
    private String branch;
    @JsonProperty(defaultValue = "N")
    private String deleAcctFlag;
    private Date lastupdated;
    private String accountType;
    private String defautAcctFlag;
}
