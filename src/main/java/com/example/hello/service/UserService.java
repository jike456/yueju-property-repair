package com.example.hello.service;

import com.example.hello.dto.AdminAddUserRequest;
import com.example.hello.dto.UserLoginRequest;
import com.example.hello.dto.UserPageQueryResult;
import com.example.hello.dto.UserRegisterRequest;

public interface UserService {

    Long register(UserRegisterRequest request);

    /**
     * 登录，返回 token 和基础信息
     */
    Object login(UserLoginRequest request);

    UserPageQueryResult pageQuery(Integer page, Integer pageSize, Integer role, Integer status, String keyword);

    Long addUser(AdminAddUserRequest request, Long operatorUserId, Integer operatorRole);

    void updateStatus(Long userId, Integer status, Long operatorUserId, Integer operatorRole);

    /**
     * 管理员删除用户（有业务关联时拒绝删除）
     */
    void deleteUser(Long userId, Long operatorUserId, Integer operatorRole);
}

