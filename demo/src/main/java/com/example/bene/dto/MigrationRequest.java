package com.example.bene.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MigrationRequest {

    private MultipartFile file;
    private String cron;
}
