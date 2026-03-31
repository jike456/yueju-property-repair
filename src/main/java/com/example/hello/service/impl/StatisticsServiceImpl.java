package com.example.hello.service.impl;

import com.example.hello.common.BusinessException;
import com.example.hello.dto.FaultTypeDistributionItem;
import com.example.hello.dto.OrderCompletionTrendItem;
import com.example.hello.dto.RepairmanRankingItem;
import com.example.hello.mapper.StatisticsMapper;
import com.example.hello.service.StatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    @Override
    public List<FaultTypeDistributionItem> getFaultTypeDistribution(String startDate, String endDate) {
        return statisticsMapper.selectFaultTypeDistribution(startDate, endDate);
    }

    @Override
    public List<OrderCompletionTrendItem> getOrderCompletionTrend(String startDate, String endDate, String interval) {
        if (!StringUtils.hasText(startDate) || !StringUtils.hasText(endDate)) {
            throw new BusinessException("开始日期和结束日期不能为空");
        }
        String intervalVal = StringUtils.hasText(interval) ? interval : "day";
        if (!"day".equals(intervalVal) && !"week".equals(intervalVal) && !"month".equals(intervalVal)) {
            intervalVal = "day";
        }
        return statisticsMapper.selectOrderCompletionTrend(startDate, endDate, intervalVal);
    }

    @Override
    public List<RepairmanRankingItem> getRepairmanRanking(String startDate, String endDate, Integer limit) {
        int limitVal = (limit == null || limit < 1) ? 10 : limit;
        return statisticsMapper.selectRepairmanRanking(startDate, endDate, limitVal);
    }
}
