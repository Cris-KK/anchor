package com.reward.audience.service;

import com.reward.audience.entity.RewardRecord;
import com.reward.audience.entity.Audience;
import com.reward.audience.mapper.RewardRecordMapper;
import com.reward.audience.mapper.AudienceMapper;
import com.reward.common.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 数据同步服务
 * 负责将打赏数据同步到财务服务和经营分析服务
 */
@Service
public class DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncService.class);

    @Autowired
    private RewardRecordMapper rewardRecordMapper;

    @Autowired
    private AudienceMapper audienceMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.finance.url}")
    private String financeServiceUrl;

    @Value("${service.analytics.url}")
    private String analyticsServiceUrl;

    @Value("${audience.reward.sync-batch-size:100}")
    private int syncBatchSize;

    /**
     * 异步同步单条打赏记录
     */
    @Async
    public CompletableFuture<Void> syncRewardRecord(RewardRecord record) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("开始异步同步打赏记录: recordId={}, traceId={}", record.getRecordId(), traceId);

        try {
            // 获取观众信息（需要性别信息）
            Audience audience = audienceMapper.selectByAudienceId(record.getAudienceId());
            if (audience == null) {
                logger.error("同步失败，观众不存在: audienceId={}", record.getAudienceId());
                rewardRecordMapper.updateSyncStatusWithRetry(record.getRecordId(), 2,
                        record.getSyncRetryCount() + 1);
                return CompletableFuture.completedFuture(null);
            }

            // 同步到财务服务
            syncToFinanceService(record, traceId);

            // 同步到经营分析服务
            syncToAnalyticsService(record, audience, traceId);

            // 更新同步状态为已同步
            rewardRecordMapper.updateSyncStatus(record.getRecordId(), 1);

            logger.info("打赏记录同步成功: recordId={}", record.getRecordId());

        } catch (Exception e) {
            logger.error("打赏记录同步失败: recordId={}, error={}", record.getRecordId(), e.getMessage(), e);
            // 更新同步状态为失败，增加重试次数
            rewardRecordMapper.updateSyncStatusWithRetry(record.getRecordId(), 2,
                    record.getSyncRetryCount() + 1);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * 定时任务：同步失败的记录重试
     */
    @Scheduled(fixedDelay = 30000) // 每30秒执行一次
    public void retryFailedSync() {
        String traceId = TraceIdUtil.getTraceId();
        logger.debug("开始重试同步失败的记录, traceId={}", traceId);

        try {
            // 查询同步失败且重试次数小于3的记录
            List<RewardRecord> failedRecords = rewardRecordMapper.findFailedSyncRecords(3, syncBatchSize);

            if (failedRecords.isEmpty()) {
                return;
            }

            logger.info("找到{}条需要重试同步的记录", failedRecords.size());

            for (RewardRecord record : failedRecords) {
                try {
                    // 设置当前线程的traceId
                    TraceIdUtil.setTraceId(record.getTraceId());

                    // 重新尝试同步
                    syncRewardRecord(record);

                } catch (Exception e) {
                    logger.error("重试同步记录失败: recordId={}, error={}",
                            record.getRecordId(), e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("重试同步任务执行失败: {}", e.getMessage(), e);
        } finally {
            TraceIdUtil.clear();
        }
    }

    /**
     * 同步到财务服务
     */
    private void syncToFinanceService(RewardRecord record, String traceId) {
        try {
            String url = financeServiceUrl + "/finance/settlement";

            Map<String, Object> request = new HashMap<>();
            request.put("recordId", record.getRecordId());
            request.put("audienceId", record.getAudienceId());
            request.put("anchorId", record.getAnchorId());
            request.put("amount", record.getAmount());
            request.put("rewardTime", record.getRewardTime());
            request.put("traceId", traceId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("traceId", traceId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            restTemplate.postForObject(url, entity, String.class);

            logger.debug("同步到财务服务成功: recordId={}", record.getRecordId());

        } catch (Exception e) {
            logger.error("同步到财务服务失败: recordId={}, error={}",
                    record.getRecordId(), e.getMessage());
            throw e;
        }
    }

    /**
     * 同步到经营分析服务
     */
    private void syncToAnalyticsService(RewardRecord record, Audience audience, String traceId) {
        try {
            String url = analyticsServiceUrl + "/analytics/reward/sync";

            Map<String, Object> request = new HashMap<>();
            request.put("recordId", record.getRecordId());
            request.put("audienceId", record.getAudienceId());
            request.put("anchorId", record.getAnchorId());
            request.put("amount", record.getAmount());
            request.put("gender", audience.getGender()); // 添加性别信息
            request.put("rewardTime", record.getRewardTime());
            request.put("traceId", traceId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("traceId", traceId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            restTemplate.postForObject(url, entity, String.class);

            logger.debug("同步到经营分析服务成功: recordId={}", record.getRecordId());

        } catch (Exception e) {
            logger.error("同步到经营分析服务失败: recordId={}, error={}",
                    record.getRecordId(), e.getMessage());
            throw e;
        }
    }

    /**
     * 批量数据同步（用于初始化或数据修复）
     */
    public void batchSyncRewardRecords(int batchSize) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("开始批量同步打赏记录, batchSize={}, traceId={}", batchSize, traceId);

        try {
            int offset = 0;
            List<RewardRecord> records;

            do {
                records = rewardRecordMapper.findUnsyncedRecords(offset, batchSize);

                for (RewardRecord record : records) {
                    syncRewardRecord(record);
                }

                offset += batchSize;
                logger.info("已处理{}条记录", offset);

                // 避免一次性处理太多数据
                if (offset > 10000) {
                    logger.info("本次批量同步达到上限，下次继续处理");
                    break;
                }

            } while (records.size() == batchSize);

            logger.info("批量同步完成, 总共处理{}条记录", offset);

        } catch (Exception e) {
            logger.error("批量同步失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}