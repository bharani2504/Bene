package com.example.bene.dto;


import lombok.Data;

@Data
public class DeleteRequest {

    private String beneNickName;
    private String delFlag;
    private String remarks;
}
