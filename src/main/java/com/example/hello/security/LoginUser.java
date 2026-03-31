package com.example.hello.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUser {

    private Long userId;
    private String username;
    /**
     * 角色：1-业主 2-维修工 3-管理员
     */
    private Integer role;
}

