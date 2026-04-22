package com.example.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class ListResponse{

    private int total;
    private String status;
    private List<Bene> data;

}
