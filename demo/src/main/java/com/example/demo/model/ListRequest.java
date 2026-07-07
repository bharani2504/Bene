package com.example.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class ListRequest {

    private boolean fetchChild;
    private List<Pagination> page;
    private List<Sorting> sort;
}
