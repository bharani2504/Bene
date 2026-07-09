package com.example.demo.model;


import lombok.Data;

@Data
public class DeleteRequest {

    private String beneNickName;
    private String delFlag;
    private String remarks;
}
