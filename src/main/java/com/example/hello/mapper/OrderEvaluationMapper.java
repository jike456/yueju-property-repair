package com.example.hello.mapper;

import com.example.hello.entity.OrderEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderEvaluationMapper {

    int insert(OrderEvaluation evaluation);

    OrderEvaluation selectByOrderId(@Param("orderId") Long orderId);
}
