package com.example.hello.mapper;

import com.example.hello.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    int insert(User user);

    int update(User user);

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    User selectByPhone(@Param("phone") String phone);

    int countByUsername(@Param("username") String username);

    int countByPhone(@Param("phone") String phone);

    List<User> selectByCondition(@Param("role") Integer role,
                                 @Param("status") Integer status,
                                 @Param("keyword") String keyword);

    int countByRoleExcludingId(@Param("role") Integer role, @Param("excludeId") Long excludeId);

    int deleteById(@Param("id") Long id);
}

