package com.reward.analytics.service.impl;

import com.reward.analytics.dto.RewardSyncDTO;
import com.reward.analytics.entity.HourlyAnalysis;
import com.reward.analytics.entity.AudienceTag;
import com.reward.analytics.entity.RewardDataSync;
import com.reward.analytics.mapper.HourlyAnalysisMapper;
import com.reward.analytics.mapper.AudienceTagMapper;
import com.reward.analytics.mapper.RewardDataSyncMapper;
import com.reward.analytics.service.AnalyticsService;
import com.reward.common.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private HourlyAnalysisMapper hourlyAnalysisMapper;
    
    @Autowired
    private AudienceTagMapper audienceTagMapper;
    
    @Autowired
    private RewardDataSyncMapper rewardDataSyncMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${analytics.cache.hourly-analysis-ttl}")
    private long hourlyAnalysisTtl;
    
    @Value("${analytics.cache.user-portrait-ttl}")
    private long userPortraitTtl;

    @Override
    public List<HourlyAnalysis> getHourlyAnalysis(String anchorId, Integer gender, Date startTime, Date endTime) {
        String cacheKey = String.format("hourly_analysis:%s:%d:%s:%s", 
            anchorId, gender, startTime.getTime(), endTime.getTime());
            
        // 尝试从缓存获取
        List<HourlyAnalysis> cachedResult = (List<HourlyAnalysis>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 从数据库查询
        List<HourlyAnalysis> result = hourlyAnalysisMapper.findByAnchorAndGenderAndTimeRange(
            anchorId, gender, startTime, endTime);
            
        // 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, hourlyAnalysisTtl, TimeUnit.SECONDS);
        
        return result;
    }

    @Override
    public Map<String, List<AudienceTag>> getUserPortrait() {
        String cacheKey = "user_portrait";
        
        // 尝试从缓存获取
        Map<String, List<AudienceTag>> cachedResult = 
            (Map<String, List<AudienceTag>>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 从数据库查询
        List<AudienceTag> allTags = audienceTagMapper.findAll();
        Map<String, List<AudienceTag>> result = allTags.stream()
            .collect(Collectors.groupingBy(AudienceTag::getTagLevel));
            
        // 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, userPortraitTtl, TimeUnit.SECONDS);
        
        return result;
    }

    @Override
    public AudienceTag getAudiencePortrait(String audienceId) {
        String cacheKey = String.format("audience_portrait:%s", audienceId);
        
        // 尝试从缓存获取
        AudienceTag cachedResult = (AudienceTag) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 从数据库查询
        AudienceTag tag = audienceTagMapper.findByAudienceId(audienceId);
        if (tag != null) {
            // 存入缓存
            redisTemplate.opsForValue().set(cacheKey, tag, userPortraitTtl, TimeUnit.SECONDS);
        }
        
        return tag;
    }

    @Override
    @Transactional
    public void syncRewardData(RewardSyncDTO syncDTO) {
        try {
            // 设置当前线程的traceId
            TraceIdUtil.setTraceId(syncDTO.getTraceId());
            
            log.info("开始处理打赏数据同步: recordId={}", syncDTO.getRecordId());
            
            // 1. 保存同步数据
            RewardDataSync sync = new RewardDataSync();
            sync.setRecordId(syncDTO.getRecordId());
            sync.setAudienceId(syncDTO.getAudienceId());
            sync.setAnchorId(syncDTO.getAnchorId());
            sync.setAmount(syncDTO.getAmount());
            sync.setGender(syncDTO.getGender());
            sync.setRewardTime(syncDTO.getRewardTime());
            sync.setTraceId(syncDTO.getTraceId());
            
            rewardDataSyncMapper.insert(sync);
            
            // 2. 更新小时维度分析
            updateHourlyAnalysis(sync);
            
            // 3. 更新观众标签
            updateAudienceTag(sync);
            
            // 4. 标记为已处理
            rewardDataSyncMapper.updateProcessed(syncDTO.getRecordId());
            
            log.info("打赏数据同步处理完成: recordId={}", syncDTO.getRecordId());
            
        } catch (Exception e) {
            log.error("打赏数据同步处理失败: recordId={}, error={}", 
                syncDTO.getRecordId(), e.getMessage(), e);
            throw e;
        } finally {
            TraceIdUtil.clear();
        }
    }

    @Override
    @Scheduled(cron = "0 0/10 * * * ?") // 每10分钟执行一次
    public void refreshAnalyticsData() {
        log.info("开始刷新分析数据");
        
        try {
            // 1. 处理未处理的同步数据
            processUnprocessedData();
            
            // 2. 重新计算小时维度分析
            calculateHourlyAnalysis();
            
            // 3. 检查是否需要重新计算全局画像
            Boolean needsRefresh = (Boolean) redisTemplate.opsForValue().get("user_portrait_needs_refresh");
            if (Boolean.TRUE.equals(needsRefresh)) {
                log.info("检测到全局画像需要刷新，开始重新计算");
                calculateUserPortrait();
                // 清除刷新标记
                redisTemplate.delete("user_portrait_needs_refresh");
                // 清除全局画像缓存
                redisTemplate.delete("user_portrait");
            } else {
                log.info("全局画像无需刷新，跳过重新计算");
            }
            
            log.info("分析数据刷新完成");
            
        } catch (Exception e) {
            log.error("分析数据刷新失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void processUnprocessedData() {
        // 获取未处理的同步数据
        List<RewardDataSync> unprocessed = rewardDataSyncMapper.findUnprocessed(1000);
        
        for (RewardDataSync sync : unprocessed) {
            try {
                // 更新小时维度分析
                updateHourlyAnalysis(sync);
                
                // 更新观众标签
                updateAudienceTag(sync);
                
                // 标记为已处理
                rewardDataSyncMapper.updateProcessed(sync.getRecordId());
                
            } catch (Exception e) {
                log.error("处理同步数据失败: recordId={}, error={}", 
                    sync.getRecordId(), e.getMessage(), e);
            }
        }
    }
    
    private void updateHourlyAnalysis(RewardDataSync sync) {
        try {
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            String hourTime = hourFormat.format(sync.getRewardTime());
            Date dateTime = new Date(sync.getRewardTime().getTime());
            
            // 先查询现有记录
            HourlyAnalysis existingAnalysis = hourlyAnalysisMapper.findByAnchorAndGenderAndHourAndDate(
                sync.getAnchorId(), sync.getGender(), hourTime, dateTime);
                
            if (existingAnalysis != null) {
                // 累加金额和次数
                existingAnalysis.setTotalAmount(existingAnalysis.getTotalAmount().add(sync.getAmount()));
                existingAnalysis.setTotalCount(existingAnalysis.getTotalCount() + 1);
                hourlyAnalysisMapper.update(existingAnalysis);
            } else {
                // 创建新记录
                HourlyAnalysis analysis = new HourlyAnalysis();
                analysis.setAnchorId(sync.getAnchorId());
                analysis.setGender(sync.getGender());
                analysis.setHourTime(hourTime);
                analysis.setDateTime(dateTime);
                analysis.setTotalAmount(sync.getAmount());
                analysis.setTotalCount(1);
                hourlyAnalysisMapper.insert(analysis);
            }
            
            // 清除相关缓存
            String cacheKey = String.format("hourly_analysis:%s:%d:*", 
                sync.getAnchorId(), sync.getGender());
            redisTemplate.delete(redisTemplate.keys(cacheKey));
            
        } catch (Exception e) {
            log.error("更新小时维度分析失败: recordId={}, error={}", 
                sync.getRecordId(), e.getMessage(), e);
            throw e;
        }
    }
    
    private void updateAudienceTag(RewardDataSync sync) {
        // 获取观众当前标签
        AudienceTag tag = audienceTagMapper.findByAudienceId(sync.getAudienceId());
        
        if (tag == null) {
            // 创建新标签
            tag = new AudienceTag();
            tag.setAudienceId(sync.getAudienceId());
            tag.setTotalAmount(sync.getAmount());
            tag.setTagLevel("medium"); // 默认设置为中等消费
            tag.setRankPercentage(new BigDecimal("50.00")); // 默认设置为50%
            audienceTagMapper.insert(tag);
        } else {
            // 更新消费总额
            tag.setTotalAmount(tag.getTotalAmount().add(sync.getAmount()));
            audienceTagMapper.update(tag);
        }
        
        // 重新计算所有观众的标签等级
        recalculateAllTagLevels();
        
        // 更新缓存策略：只更新单个观众的缓存，而不是删除所有缓存
        String audienceCacheKey = String.format("audience_portrait:%s", sync.getAudienceId());
        redisTemplate.delete(audienceCacheKey);
        
        // 设置一个标记，表示需要重新计算全局画像
        redisTemplate.opsForValue().set("user_portrait_needs_refresh", true, 1, TimeUnit.HOURS);
    }
    
    private void recalculateAllTagLevels() {
        // 获取所有观众的消费总额
        List<AudienceTag> allTags = audienceTagMapper.findAll();
        
        if (allTags.isEmpty()) {
            return;
        }
        
        // 按消费金额排序
        allTags.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));
        
        // 计算分位数，标记高/中/低消费人群
        int totalCount = allTags.size();
        
        for (int i = 0; i < totalCount; i++) {
            AudienceTag tag = allTags.get(i);
            
            // 计算排名百分比
            BigDecimal percentage = new BigDecimal(i + 1)
                .multiply(new BigDecimal("100"))
                .divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
                
            tag.setRankPercentage(percentage);
            
            // 设置标签等级
            if (percentage.compareTo(new BigDecimal("20")) <= 0) {
                tag.setTagLevel("high");
            } else if (percentage.compareTo(new BigDecimal("80")) <= 0) {
                tag.setTagLevel("medium");
            } else {
                tag.setTagLevel("low");
            }
            
            // 更新标签
            audienceTagMapper.updateTagLevel(tag);
        }
    }
    
    private void calculateHourlyAnalysis() {
        log.info("开始重新计算小时维度分析数据");
        try {
            // 1. 清空hourly_analysis表
            hourlyAnalysisMapper.truncate();
            
            // 2. 获取所有打赏记录
            List<RewardDataSync> allRewards = rewardDataSyncMapper.findByTimeRange(
                new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000), // 最近30天
                new Date()
            );
            
            // 3. 按主播、性别、小时分组统计
            Map<String, Map<Integer, Map<String, HourlyAnalysis>>> analysisMap = new HashMap<>();
            
            for (RewardDataSync reward : allRewards) {
                String anchorId = reward.getAnchorId();
                Integer gender = reward.getGender();
                String hourTime = new SimpleDateFormat("HH").format(reward.getRewardTime());
                Date dateTime = new Date(reward.getRewardTime().getTime());
                
                // 获取或创建分析对象
                HourlyAnalysis analysis = analysisMap
                    .computeIfAbsent(anchorId, k -> new HashMap<>())
                    .computeIfAbsent(gender, k -> new HashMap<>())
                    .computeIfAbsent(hourTime + "_" + dateTime, k -> {
                        HourlyAnalysis a = new HourlyAnalysis();
                        a.setAnchorId(anchorId);
                        a.setGender(gender);
                        a.setHourTime(hourTime);
                        a.setDateTime(dateTime);
                        a.setTotalAmount(BigDecimal.ZERO);
                        a.setTotalCount(0);
                        return a;
                    });
                
                // 累加打赏金额和次数
                analysis.setTotalAmount(analysis.getTotalAmount().add(reward.getAmount()));
                analysis.setTotalCount(analysis.getTotalCount() + 1);
            }
            
            // 4. 批量插入hourly_analysis表
            List<HourlyAnalysis> analysisList = new ArrayList<>();
            analysisMap.values().stream()
                .flatMap(genderMap -> genderMap.values().stream())
                .flatMap(hourMap -> hourMap.values().stream())
                .forEach(analysisList::add);
                
            if (!analysisList.isEmpty()) {
                hourlyAnalysisMapper.batchInsert(analysisList);
            }
            
            log.info("小时维度分析数据重新计算完成，共处理{}条记录", analysisList.size());
            
        } catch (Exception e) {
            log.error("重新计算小时维度分析数据失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void calculateUserPortrait() {
        log.info("开始重新计算观众画像数据");
        try {
            // 1. 获取所有观众的消费总额
            List<AudienceTag> allTags = audienceTagMapper.findAll();
            
            if (allTags.isEmpty()) {
                log.info("没有观众数据需要处理");
                return;
            }
            
            // 2. 按消费金额排序
            allTags.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));
            
            // 3. 计算分位数，标记高/中/低消费人群
            int totalCount = allTags.size();
            List<AudienceTag> updatedTags = new ArrayList<>();
            
            for (int i = 0; i < totalCount; i++) {
                AudienceTag tag = allTags.get(i);
                
                // 计算排名百分比
                BigDecimal percentage = new BigDecimal(i + 1)
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
                    
                tag.setRankPercentage(percentage);
                
                // 设置标签等级
                if (percentage.compareTo(new BigDecimal("20")) <= 0) {
                    tag.setTagLevel("high");
                } else if (percentage.compareTo(new BigDecimal("80")) <= 0) {
                    tag.setTagLevel("medium");
                } else {
                    tag.setTagLevel("low");
                }
                
                updatedTags.add(tag);
            }
            
            // 4. 批量更新audience_tag表
            if (!updatedTags.isEmpty()) {
                audienceTagMapper.batchUpdate(updatedTags);
            }
            
            log.info("观众画像数据重新计算完成，共处理{}条记录", updatedTags.size());
            
        } catch (Exception e) {
            log.error("重新计算观众画像数据失败: {}", e.getMessage(), e);
            throw e;
        }
    }
} 