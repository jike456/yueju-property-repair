package com.example.hello.mapper;

import com.example.hello.entity.Repairman;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RepairmanMapper {

    int insert(Repairman repairman);

    Repairman selectByUserId(@Param("userId") Long userId);

    Repairman selectById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);
}

