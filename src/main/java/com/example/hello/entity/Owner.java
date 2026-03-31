package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class Owner {

    private Long id;

    private Long userId;

    private String buildingNo;

    private String unitNo;

    private String roomNo;

    private BigDecimal area;

    private LocalDate moveInDate;

    private LocalDateTime createTime;
}

