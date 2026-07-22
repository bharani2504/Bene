package com.example.bene.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BeneDeletedResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("beneNickName")
    private String beneNickName;

    @JsonProperty("deletedAt")
    private Date deletedAt;
}
