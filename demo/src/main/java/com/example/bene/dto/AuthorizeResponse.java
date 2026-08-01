package com.example.bene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

@Data
public class AuthorizeResponse {

    @JsonProperty("status")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonSetter(nulls = Nulls.FAIL)
    private String status;

    @JsonProperty("beneNickName")
    private String beneNickName;


}
