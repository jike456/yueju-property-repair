package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.dto.FaultTypeDistributionItem;
import com.example.hello.dto.OrderCompletionTrendItem;
import com.example.hello.dto.RepairmanRankingItem;
import com.example.hello.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * 6.1 报修类型分布
     */
    @GetMapping("/fault-type-distribution")
    public ApiResponse<List<FaultTypeDistributionItem>> getFaultTypeDistribution(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<FaultTypeDistributionItem> result = statisticsService.getFaultTypeDistribution(startDate, endDate);
        return ApiResponse.success(result);
    }

    /**
     * 6.2 工单完成率趋势
     */
    @GetMapping("/order-completion-trend")
    public ApiResponse<List<OrderCompletionTrendItem>> getOrderCompletionTrend(
            @RequestParam(required = true) String startDate,
            @RequestParam(required = true) String endDate,
            @RequestParam(required = false, defaultValue = "day") String interval) {
        List<OrderCompletionTrendItem> result = statisticsService.getOrderCompletionTrend(startDate, endDate, interval);
        return ApiResponse.success(result);
    }

    /**
     * 6.3 维修工绩效排行
     */
    @GetMapping("/repairman-ranking")
    public ApiResponse<List<RepairmanRankingItem>> getRepairmanRanking(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<RepairmanRankingItem> result = statisticsService.getRepairmanRanking(startDate, endDate, limit);
        return ApiResponse.success(result);
    }
}
