package com.example.bene.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticalRequest {

    private List<Filter> filters;

}
