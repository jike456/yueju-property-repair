package com.example.hello.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DispatchOrderRequest {

    @NotNull(message = "维修工ID不能为空")
    private Long repairmanId;
}
