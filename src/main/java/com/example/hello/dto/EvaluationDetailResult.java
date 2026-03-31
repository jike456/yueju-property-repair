package com.example.hello.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EvaluationDetailResult {

    private Long id;
    private Integer score;
    private String content;
    private List<String> images;
    private LocalDateTime createTime;
    /**
     * 业主昵称/姓名（非匿名时返回）
     */
    private String ownerName;
}
