package com.example.hello.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderPageResult {

    private long total;
    private List<OrderItem> rows;

    @Data
    public static class OrderItem {
        private Long id;
        private String orderNo;
        private String title;
        private String faultTypeName;
        private Integer status;
        private Integer priority;
        private LocalDateTime createTime;
        private LocalDateTime appointmentTime;
        private String repairmanName;
        private String ownerName;
    }
}
