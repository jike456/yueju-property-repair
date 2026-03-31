package com.example.hello.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResult {

    private Long id;
    private String orderNo;
    private String title;
    private String faultTypeName;
    private String description;
    private List<String> images;
    private Integer status;
    private Integer priority;
    private LocalDateTime createTime;
    private LocalDateTime appointmentTime;
    private String addressDetail;

    private OwnerInfo owner;
    private ProcessInfo process;
    private EvaluationInfo evaluation;

    @Data
    public static class OwnerInfo {
        private String realName;
        private String phone;
        private String address;
    }

    @Data
    public static class ProcessInfo {
        private String repairmanName;
        private LocalDateTime assignTime;
        private LocalDateTime acceptTime;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String processNote;
        private List<String> processImages;
        private String materialUsed;
    }

    @Data
    public static class EvaluationInfo {
        private Integer score;
        private String content;
        private List<String> images;
        private LocalDateTime createTime;
    }
}
