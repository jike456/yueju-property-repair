package com.example.hello.service;

import com.example.hello.dto.AnnouncementPageResult;
import com.example.hello.dto.AnnouncementUpsertRequest;

public interface AnnouncementService {

    AnnouncementPageResult pageQuery(Integer page, Integer pageSize, Integer status);

    AnnouncementPageResult.AnnouncementItem[] getLatest(Integer limit);

    AnnouncementPageResult.AnnouncementItem getById(Long id);

    Long create(AnnouncementUpsertRequest request, Long currentUserId, Integer currentUserRole);

    void update(Long id, AnnouncementUpsertRequest request, Long currentUserId, Integer currentUserRole);

    void publish(Long id, Long currentUserId, Integer currentUserRole);
}
