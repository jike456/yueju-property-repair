package com.example.hello.service.impl;

import com.example.hello.common.BusinessException;
import com.example.hello.dto.AnnouncementPageResult;
import com.example.hello.dto.AnnouncementUpsertRequest;
import com.example.hello.entity.Announcement;
import com.example.hello.mapper.AnnouncementMapper;
import com.example.hello.service.AnnouncementService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementServiceImpl(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    @Override
    public AnnouncementPageResult pageQuery(Integer page, Integer pageSize, Integer status) {
        int pageIndex = (page == null || page < 1) ? 1 : page;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        PageHelper.startPage(pageIndex, size);
        List<Announcement> list = announcementMapper.selectByCondition(status);
        PageInfo<Announcement> pageInfo = new PageInfo<>(list);

        AnnouncementPageResult result = new AnnouncementPageResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(list.stream().map(this::toItem).collect(Collectors.toList()));
        return result;
    }

    @Override
    public AnnouncementPageResult.AnnouncementItem[] getLatest(Integer limit) {
        int limitVal = (limit == null || limit < 1) ? 5 : limit;
        List<Announcement> list = announcementMapper.selectLatest(limitVal);
        return list.stream().map(this::toItem).toArray(AnnouncementPageResult.AnnouncementItem[]::new);
    }

    @Override
    public AnnouncementPageResult.AnnouncementItem getById(Long id) {
        if (id == null) {
            throw new BusinessException("公告 ID 不能为空");
        }
        Announcement a = announcementMapper.selectById(id);
        if (a == null) {
            throw new BusinessException("公告不存在");
        }
        return toItem(a);
    }

    @Override
    @Transactional
    public Long create(AnnouncementUpsertRequest request, Long currentUserId, Integer currentUserRole) {
        requireAdmin(currentUserRole);
        int status = request.getStatus() != null ? request.getStatus() : 0;
        if (status != 0 && status != 1) {
            throw new BusinessException("状态只能为 0（草稿）或 1（已发布）");
        }

        LocalDateTime now = LocalDateTime.now();
        Announcement a = new Announcement();
        a.setTitle(request.getTitle().trim());
        a.setContent(request.getContent().trim());
        a.setPublisherId(currentUserId);
        a.setExpireTime(parseExpireTime(request.getExpireTime()));
        a.setTopStatus(request.getTopStatus() != null && request.getTopStatus() == 1 ? 1 : 0);
        a.setStatus(status);
        a.setViewCount(0);
        a.setCreateTime(now);
        // 表字段 publish_time 多为 NOT NULL：草稿也写入时间，以 status=0 区分未发布
        a.setPublishTime(now);

        announcementMapper.insert(a);
        return a.getId();
    }

    @Override
    @Transactional
    public void update(Long id, AnnouncementUpsertRequest request, Long currentUserId, Integer currentUserRole) {
        requireAdmin(currentUserRole);
        Announcement existing = announcementMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }

        Integer reqStatus = request.getStatus();
        int status;
        if (reqStatus != null) {
            if (reqStatus != 0 && reqStatus != 1 && reqStatus != 2) {
                throw new BusinessException("状态只能为 0（草稿）、1（已发布）或 2（已过期）");
            }
            status = reqStatus;
        } else {
            status = existing.getStatus() != null ? existing.getStatus() : 0;
        }

        LocalDateTime now = LocalDateTime.now();
        Announcement a = new Announcement();
        a.setId(id);
        a.setTitle(request.getTitle().trim());
        a.setContent(request.getContent().trim());
        a.setExpireTime(parseExpireTime(request.getExpireTime()));

        int top = request.getTopStatus() != null && request.getTopStatus() == 1 ? 1 : 0;
        a.setTopStatus(top);
        a.setStatus(status);

        if (status == 1) {
            Integer prev = existing.getStatus();
            if (prev != null && prev == 1 && existing.getPublishTime() != null) {
                a.setPublishTime(existing.getPublishTime());
            } else {
                a.setPublishTime(now);
            }
        } else if (status == 0) {
            a.setPublishTime(existing.getPublishTime() != null ? existing.getPublishTime() : now);
        } else {
            a.setPublishTime(existing.getPublishTime());
        }

        announcementMapper.update(a);
    }

    @Override
    @Transactional
    public void publish(Long id, Long currentUserId, Integer currentUserRole) {
        requireAdmin(currentUserRole);
        Announcement existing = announcementMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        if (existing.getStatus() != null && existing.getStatus() == 1) {
            throw new BusinessException("公告已是发布状态");
        }

        LocalDateTime now = LocalDateTime.now();
        Announcement a = new Announcement();
        a.setId(id);
        a.setTitle(existing.getTitle());
        a.setContent(existing.getContent());
        a.setExpireTime(existing.getExpireTime());
        a.setTopStatus(existing.getTopStatus() != null ? existing.getTopStatus() : 0);
        a.setStatus(1);
        a.setPublishTime(now);
        announcementMapper.update(a);
    }

    private void requireAdmin(Integer role) {
        if (role == null || role != 3) {
            throw new BusinessException("只有管理员可以操作公告");
        }
    }

    private LocalDateTime parseExpireTime(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String t = s.trim().replace(" ", "T");
        try {
            return LocalDateTime.parse(t);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(s.trim(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception ignored) {
                throw new BusinessException("过期时间格式无效，请使用 yyyy-MM-dd HH:mm:ss");
            }
        }
    }

    private AnnouncementPageResult.AnnouncementItem toItem(Announcement a) {
        AnnouncementPageResult.AnnouncementItem item = new AnnouncementPageResult.AnnouncementItem();
        item.setId(a.getId());
        item.setTitle(a.getTitle());
        item.setContent(a.getContent());
        item.setPublishTime(a.getPublishTime());
        item.setExpireTime(a.getExpireTime());
        item.setStatus(a.getStatus());
        item.setTopStatus(a.getTopStatus());
        item.setViewCount(a.getViewCount());
        return item;
    }
}
