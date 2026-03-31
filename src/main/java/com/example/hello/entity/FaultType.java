package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FaultType {

    private Long id;

    private String typeName;

    private Long parentId;

    /**
     * 层级：1-一级 2-二级
     */
    private Integer level;

    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status = 1;

    private LocalDateTime createTime;

    /**
     * 子类型列表（树形结构用，非表字段）
     */
    private List<FaultType> children;
}
