package com.example.hello.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubmitEvaluationRequest {

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分1-5分")
    @Max(value = 5, message = "评分1-5分")
    private Integer score;

    private String content;

    private List<String> images;

    private Boolean anonymous;
}
