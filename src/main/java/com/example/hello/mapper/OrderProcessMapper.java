package com.example.hello.mapper;

import com.example.hello.entity.OrderProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderProcessMapper {

    int insert(OrderProcess process);

    int update(OrderProcess process);

    OrderProcess selectByOrderId(@Param("orderId") Long orderId);

    int countByRepairmanId(@Param("repairmanId") Long repairmanId);

    int clearAssignerByUserId(@Param("userId") Long userId);
}
