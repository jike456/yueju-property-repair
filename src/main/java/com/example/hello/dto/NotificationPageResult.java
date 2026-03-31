package com.example.hello.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NotificationPageResult {

    private long total;
    private List<NotificationItem> rows;

    @Data
    public static class NotificationItem {
        private Long id;
        private Integer type;
        private String title;
        private String content;
        private Integer isRead;
        private LocalDateTime createTime;
    }
}
