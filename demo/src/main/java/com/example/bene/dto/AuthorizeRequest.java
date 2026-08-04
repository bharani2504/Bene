package com.example.bene.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthorizeRequest {


    @JsonProperty("beneNickName")
    private String beneNickName;

    @JsonProperty("action")
    private String action;

    @JsonProperty("rejectReason")
    private String rejectReason;

    @JsonProperty("referenceId")
    private String referenceId;

    @JsonProperty("mfaToken")
    private String mfaToken;

    @JsonProperty("otp")
    private String otp;

}

