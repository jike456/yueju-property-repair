package com.example.hello.service;

import com.example.hello.dto.FaultTypeDistributionItem;
import com.example.hello.dto.OrderCompletionTrendItem;
import com.example.hello.dto.RepairmanRankingItem;

import java.util.List;

public interface StatisticsService {

    List<FaultTypeDistributionItem> getFaultTypeDistribution(String startDate, String endDate);

    List<OrderCompletionTrendItem> getOrderCompletionTrend(String startDate, String endDate, String interval);

    List<RepairmanRankingItem> getRepairmanRanking(String startDate, String endDate, Integer limit);
}
