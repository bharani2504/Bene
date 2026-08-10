package com.example.bene.dto;

import lombok.Data;

@Data
public class DownloadResponse {

    private String status;
    private String fileName;
    private String docContent;
    private String mimeType;
}
