package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Repairman {

    private Long id;

    private Long userId;

    private String skillType;

    private Integer workYears;

    /**
     * 工作状态：0-空闲 1-忙碌 2-休假
     */
    private Integer workStatus = 0;

    private BigDecimal score = new BigDecimal("5.00");

    private Integer totalOrders = 0;

    private LocalDateTime createTime;
}

