package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.dto.NotificationPageResult;
import com.example.hello.security.LoginUser;
import com.example.hello.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 7.1 分页查询当前用户的消息
     */
    @GetMapping
    public ApiResponse<NotificationPageResult> pageQuery(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer isRead) {
        LoginUser current = getCurrentUser();
        NotificationPageResult result = notificationService.pageQuery(page, pageSize, isRead, current.getUserId());
        return ApiResponse.success(result);
    }

    /**
     * 7.2 标记消息已读
     */
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable("id") Long id) {
        LoginUser current = getCurrentUser();
        notificationService.markRead(id, current.getUserId());
        return ApiResponse.success(null);
    }

    /**
     * 7.3 标记所有消息已读
     */
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllRead() {
        LoginUser current = getCurrentUser();
        notificationService.markAllRead(current.getUserId());
        return ApiResponse.success(null);
    }

    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return new LoginUser(null, null, null);
        }
        return (LoginUser) authentication.getPrincipal();
    }
}
