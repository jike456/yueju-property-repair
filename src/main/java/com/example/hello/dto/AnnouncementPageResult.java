package com.example.hello.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnnouncementPageResult {

    private long total;
    private List<AnnouncementItem> rows;

    @Data
    public static class AnnouncementItem {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime publishTime;
        private LocalDateTime expireTime;
        private Integer status;
        private Integer topStatus;
        private Integer viewCount;
    }
}
