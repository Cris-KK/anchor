package com.reward.analytics.service;

import com.reward.analytics.dto.RewardSyncDTO;
import com.reward.analytics.entity.HourlyAnalysis;
import com.reward.analytics.entity.AudienceTag;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    // 按小时、性别、主播查询分析数据
    List<HourlyAnalysis> getHourlyAnalysis(String anchorId, Integer gender, Date startTime, Date endTime);
    
    // 获取观众画像
    Map<String, List<AudienceTag>> getUserPortrait();
    
    // 同步打赏数据
    void syncRewardData(RewardSyncDTO syncDTO);
    
    // 刷新分析数据
    void refreshAnalyticsData();

    AudienceTag getAudiencePortrait(String audienceId);
} 