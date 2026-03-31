package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderProcess {

    private Long id;

    private Long orderId;

    private Long repairmanId;

    private Long assignerId;

    private LocalDateTime assignTime;

    private LocalDateTime acceptTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String processNote;

    /**
     * 处理过程图片 JSON 数组
     */
    private String processImages;

    private String materialUsed;

    private LocalDateTime createTime;
}
