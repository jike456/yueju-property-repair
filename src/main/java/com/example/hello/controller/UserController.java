package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.dto.AdminAddUserRequest;
import com.example.hello.dto.UpdateUserStatusRequest;
import com.example.hello.dto.UserLoginRequest;
import com.example.hello.dto.UserPageQueryResult;
import com.example.hello.dto.UserRegisterRequest;
import com.example.hello.security.LoginUser;
import com.example.hello.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 1.1 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody UserRegisterRequest request) {
        Long userId = userService.register(request);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        return ApiResponse.success("注册成功", data);
    }

    /**
     * 1.2 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Object> login(@Valid @RequestBody UserLoginRequest request) {
        Object data = userService.login(request);
        return ApiResponse.success("登录成功", data);
    }

    /**
     * 1.3 分页查询用户（管理员）
     */
    @GetMapping
    public ApiResponse<UserPageQueryResult> pageQuery(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                      @RequestParam(required = false) Integer role,
                                                      @RequestParam(required = false) Integer status,
                                                      @RequestParam(required = false) String keyword) {
        UserPageQueryResult result = userService.pageQuery(page, pageSize, role, status, keyword);
        return ApiResponse.success(result);
    }

    /**
     * 1.4 添加用户（管理员）
     */
    @PostMapping
    public ApiResponse<Map<String, Object>> addUser(@Valid @RequestBody AdminAddUserRequest request) {
        LoginUser current = getCurrentUser();
        Long id = userService.addUser(request, current.getUserId(), current.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("userId", id);
        return ApiResponse.success("新增成功", data);
    }

    /**
     * 1.5 修改用户状态（管理员）
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable("id") Long id,
                                          @Valid @RequestBody UpdateUserStatusRequest request) {
        LoginUser current = getCurrentUser();
        userService.updateStatus(id, request.getStatus(), current.getUserId(), current.getRole());
        return ApiResponse.success(null);
    }

    /**
     * 1.6 删除用户（管理员）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable("id") Long id) {
        LoginUser current = getCurrentUser();
        userService.deleteUser(id, current.getUserId(), current.getRole());
        return ApiResponse.success("删除成功", null);
    }

    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return new LoginUser(null, null, null);
        }
        return (LoginUser) authentication.getPrincipal();
    }
}

