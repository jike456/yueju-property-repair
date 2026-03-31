package com.example.hello.mapper;

import com.example.hello.dto.FaultTypeDistributionItem;
import com.example.hello.dto.OrderCompletionTrendItem;
import com.example.hello.dto.RepairmanRankingItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    List<FaultTypeDistributionItem> selectFaultTypeDistribution(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    List<OrderCompletionTrendItem> selectOrderCompletionTrend(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("interval") String interval);

    List<RepairmanRankingItem> selectRepairmanRanking(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("limit") Integer limit);
}
