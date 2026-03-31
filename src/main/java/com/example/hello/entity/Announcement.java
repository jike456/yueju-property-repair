package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Announcement {

    private Long id;

    private String title;

    private String content;

    private Long publisherId;

    private LocalDateTime publishTime;

    private LocalDateTime expireTime;

    /**
     * 状态：0-草稿 1-已发布 2-已过期
     */
    private Integer status = 1;

    /**
     * 置顶：0-否 1-是
     */
    private Integer topStatus = 0;

    private Integer viewCount = 0;

    private LocalDateTime createTime;
}
