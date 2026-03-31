package com.example.hello.service;

import com.example.hello.dto.FaultTypeTreeNode;

import java.util.List;

public interface FaultTypeService {

    List<FaultTypeTreeNode> getTree();
}
