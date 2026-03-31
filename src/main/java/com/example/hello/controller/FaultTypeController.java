package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.dto.FaultTypeTreeNode;
import com.example.hello.service.FaultTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fault-types")
public class FaultTypeController {

    private final FaultTypeService faultTypeService;

    public FaultTypeController(FaultTypeService faultTypeService) {
        this.faultTypeService = faultTypeService;
    }

    /**
     * 3.1 获取故障类型树
     */
    @GetMapping("/tree")
    public ApiResponse<List<FaultTypeTreeNode>> getTree() {
        List<FaultTypeTreeNode> tree = faultTypeService.getTree();
        return ApiResponse.success(tree);
    }
}
