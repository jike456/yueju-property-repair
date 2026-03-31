package com.example.hello.mapper;

import com.example.hello.entity.FaultType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FaultTypeMapper {

    List<FaultType> selectByParentId(@Param("parentId") Long parentId);

    List<FaultType> selectAllEnabled();

    FaultType selectById(@Param("id") Long id);
}
