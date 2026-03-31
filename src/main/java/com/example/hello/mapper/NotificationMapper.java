package com.example.hello.mapper;

import com.example.hello.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    int insert(Notification notification);

    int update(Notification notification);

    Notification selectById(@Param("id") Long id);

    List<Notification> selectByReceiverId(@Param("receiverId") Long receiverId,
                                          @Param("isRead") Integer isRead);

    int updateReadByReceiverId(@Param("receiverId") Long receiverId);

    int deleteByReceiverOrSender(@Param("userId") Long userId);
}
