package com.reward.analytics.controller;

import com.reward.analytics.dto.RewardSyncDTO;
import com.reward.analytics.entity.HourlyAnalysis;
import com.reward.analytics.entity.AudienceTag;
import com.reward.analytics.service.AnalyticsService;
import com.reward.common.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/hourly")
    public List<HourlyAnalysis> getHourlyAnalysis(
            @RequestParam String anchorId,
            @RequestParam Integer gender,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return analyticsService.getHourlyAnalysis(anchorId, gender, startTime, endTime);
    }

    @GetMapping("/portrait")
    public Map<String, List<AudienceTag>> getUserPortrait() {
        return analyticsService.getUserPortrait();
    }

    @PostMapping("/reward/sync")
    public void syncRewardData(@RequestBody RewardSyncDTO syncDTO) {
        log.info("接收到打赏数据同步请求: recordId={}, traceId={}", 
            syncDTO.getRecordId(), syncDTO.getTraceId());
        analyticsService.syncRewardData(syncDTO);
    }

    @PostMapping("/refresh")
    public void refreshAnalyticsData() {
        analyticsService.refreshAnalyticsData();
    }
} 