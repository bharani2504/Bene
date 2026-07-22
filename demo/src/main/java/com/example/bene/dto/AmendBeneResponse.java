package com.example.bene.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AmendBeneResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("beneNickName")
    private String beneNickName;

    @JsonProperty("createdDate")
    private Date lastUpdatedat;
}
