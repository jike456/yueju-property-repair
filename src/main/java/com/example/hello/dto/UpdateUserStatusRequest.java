package com.example.hello.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {

    /**
     * 0-禁用 1-启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}

