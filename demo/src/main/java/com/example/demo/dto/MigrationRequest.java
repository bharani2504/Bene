package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MigrationRequest {

    @JsonProperty("file")
    private MultipartFile file;

    @JsonProperty("schedule")
    private String schedule;
}
