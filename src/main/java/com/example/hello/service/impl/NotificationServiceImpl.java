package com.example.hello.service.impl;

import com.example.hello.common.BusinessException;
import com.example.hello.dto.NotificationPageResult;
import com.example.hello.entity.Notification;
import com.example.hello.mapper.NotificationMapper;
import com.example.hello.service.NotificationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    @Override
    public NotificationPageResult pageQuery(Integer page, Integer pageSize, Integer isRead, Long currentUserId) {
        if (currentUserId == null) {
            throw new BusinessException("请先登录");
        }

        int pageIndex = (page == null || page < 1) ? 1 : page;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        PageHelper.startPage(pageIndex, size);
        List<Notification> list = notificationMapper.selectByReceiverId(currentUserId, isRead);
        PageInfo<Notification> pageInfo = new PageInfo<>(list);

        NotificationPageResult result = new NotificationPageResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(list.stream().map(this::toItem).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void markRead(Long notificationId, Long currentUserId) {
        if (currentUserId == null) {
            throw new BusinessException("请先登录");
        }
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException("消息不存在");
        }
        if (!currentUserId.equals(notification.getReceiverId())) {
            throw new BusinessException("无权限操作该消息");
        }
        notification.setIsRead(1);
        notification.setReadTime(LocalDateTime.now());
        notificationMapper.update(notification);
    }

    @Override
    @Transactional
    public void markAllRead(Long currentUserId) {
        if (currentUserId == null) {
            throw new BusinessException("请先登录");
        }
        notificationMapper.updateReadByReceiverId(currentUserId);
    }

    private NotificationPageResult.NotificationItem toItem(Notification n) {
        NotificationPageResult.NotificationItem item = new NotificationPageResult.NotificationItem();
        item.setId(n.getId());
        item.setType(n.getType());
        item.setTitle(n.getTitle());
        item.setContent(n.getContent());
        item.setIsRead(n.getIsRead());
        item.setCreateTime(n.getCreateTime());
        return item;
    }
}
