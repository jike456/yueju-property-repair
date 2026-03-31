package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotNull(message = "故障类型ID不能为空")
    private Long faultTypeId;

    @NotBlank(message = "报修标题不能为空")
    private String title;

    @NotBlank(message = "故障描述不能为空")
    private String description;

    private List<String> images;

    private String appointmentTime;

    private String addressDetail;

    /**
     * 优先级：1-低 2-中 3-高 4-紧急，默认2
     */
    private Integer priority;
}
