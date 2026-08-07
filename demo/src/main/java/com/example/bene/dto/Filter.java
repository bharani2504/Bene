package com.example.bene.dto;

import lombok.Data;

@Data
public class Filter {

       private  String name ;
       private String value ;

    public Filter(String name, String value) {
        this.name = name;
        this.value=value;
    }
}
