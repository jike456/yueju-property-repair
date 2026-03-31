package com.example.hello.dto;

import lombok.Data;

import java.util.List;

@Data
public class FaultTypeTreeNode {

    private Long id;
    private String typeName;
    private List<FaultTypeTreeNode> children;
}
