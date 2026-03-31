package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.dto.AnnouncementPageResult;
import com.example.hello.dto.AnnouncementUpsertRequest;
import com.example.hello.security.LoginUser;
import com.example.hello.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 2.1 分页查询公告
     */
    @GetMapping
    public ApiResponse<AnnouncementPageResult> pageQuery(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        AnnouncementPageResult result = announcementService.pageQuery(page, pageSize, status);
        return ApiResponse.success(result);
    }

    /**
     * 2.2 获取最新公告
     */
    @GetMapping("/latest")
    public ApiResponse<AnnouncementPageResult.AnnouncementItem[]> getLatest(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        AnnouncementPageResult.AnnouncementItem[] items = announcementService.getLatest(limit);
        return ApiResponse.success(items);
    }

    /**
     * 2.3 公告详情（编辑用）
     */
    @GetMapping("/{id}")
    public ApiResponse<AnnouncementPageResult.AnnouncementItem> getById(@PathVariable Long id) {
        return ApiResponse.success(announcementService.getById(id));
    }

    /**
     * 2.4 新增公告（管理员）
     */
    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody AnnouncementUpsertRequest request) {
        LoginUser current = getCurrentUser();
        Long id = announcementService.create(request, current.getUserId(), current.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return ApiResponse.success("新增成功", data);
    }

    /**
     * 2.5 编辑公告（管理员）
     */
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody AnnouncementUpsertRequest request) {
        LoginUser current = getCurrentUser();
        announcementService.update(id, request, current.getUserId(), current.getRole());
        return ApiResponse.success("保存成功", null);
    }

    /**
     * 2.6 发布公告（草稿 → 已发布，管理员）
     */
    @PostMapping("/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable Long id) {
        LoginUser current = getCurrentUser();
        announcementService.publish(id, current.getUserId(), current.getRole());
        return ApiResponse.success("发布成功", null);
    }

    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return new LoginUser(null, null, null);
        }
        return (LoginUser) authentication.getPrincipal();
    }
}
