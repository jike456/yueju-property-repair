package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RepairOrder {

    private Long id;

    private String orderNo;

    private Long ownerId;

    private Long faultTypeId;

    private String title;

    private String description;

    /**
     * 报修图片 JSON 数组
     */
    private String images;

    private LocalDateTime appointmentTime;

    private String addressDetail;

    /**
     * 状态：0-待派单 1-待处理 2-处理中 3-待确认 4-已完成 5-已取消 6-已拒绝
     */
    private Integer status = 0;

    /**
     * 优先级：1-低 2-中 3-高 4-紧急
     */
    private Integer priority = 2;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
