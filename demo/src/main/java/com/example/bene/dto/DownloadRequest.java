package com.example.bene.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DownloadRequest {

    @JsonProperty("request")
    private ListRequest request;

    @JsonProperty("mimeType")
    private String mimeType;

    @JsonProperty("pageCode")
    private String pageCode;
}
