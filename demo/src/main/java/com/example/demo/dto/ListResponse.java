package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListResponse{

    private int total;
    private String status;
    private List<Bene> data;

}
