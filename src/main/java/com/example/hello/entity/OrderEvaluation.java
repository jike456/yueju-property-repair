package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderEvaluation {

    private Long id;

    private Long orderId;

    private Long ownerId;

    private Long repairmanId;

    private Integer score;

    private String content;

    private String images;

    private Integer anonymous;

    private LocalDateTime createTime;
}
