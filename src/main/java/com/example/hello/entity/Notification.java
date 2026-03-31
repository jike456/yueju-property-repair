package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Notification {

    private Long id;

    private Long receiverId;

    private Long senderId;

    /**
     * 消息类型：1-系统通知 2-工单提醒 3-评价提醒
     */
    private Integer type;

    private String title;

    private String content;

    private String data;

    /**
     * 是否已读：0-未读 1-已读
     */
    private Integer isRead = 0;

    private LocalDateTime createTime;

    private LocalDateTime readTime;
}
