package com.example.hello.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompleteOrderRequest {

    private String processNote;

    private List<String> processImages;

    private String materialUsed;
}
