package com.example.hello.dto;

import lombok.Data;

@Data
public class OrderCompletionTrendItem {

    private String date;
    private Long total;
    private Long completed;
}
