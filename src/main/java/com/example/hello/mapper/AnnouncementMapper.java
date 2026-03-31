package com.example.hello.mapper;

import com.example.hello.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    List<Announcement> selectByCondition(@Param("status") Integer status);

    List<Announcement> selectLatest(@Param("limit") int limit);

    Announcement selectById(@Param("id") Long id);

    int insert(Announcement row);

    int update(Announcement row);
}
