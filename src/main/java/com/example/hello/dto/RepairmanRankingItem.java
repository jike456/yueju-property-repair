package com.example.hello.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepairmanRankingItem {

    private Long repairmanId;
    private String repairmanName;
    private Long completedOrders;
    private BigDecimal avgScore;
}
