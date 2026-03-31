package com.example.hello.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {

    private Long id;

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String idCard;

    private String avatar;

    /**
     * 角色：1-业主 2-维修工 3-管理员
     */
    private Integer role;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status = 1;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime lastLoginTime;
}

