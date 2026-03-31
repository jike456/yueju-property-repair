package com.example.hello.mapper;

import com.example.hello.entity.Owner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OwnerMapper {

    int insert(Owner owner);

    Owner selectByUserId(@Param("userId") Long userId);

    Owner selectById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);
}

