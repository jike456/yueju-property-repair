package com.example.hello.service;

import com.example.hello.dto.NotificationPageResult;

public interface NotificationService {

    NotificationPageResult pageQuery(Integer page, Integer pageSize, Integer isRead, Long currentUserId);

    void markRead(Long notificationId, Long currentUserId);

    void markAllRead(Long currentUserId);
}
