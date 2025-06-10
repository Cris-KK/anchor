package com.reward.audience.service;

import com.reward.audience.entity.Audience;
import com.reward.audience.mapper.AudienceMapper;
import com.reward.common.exception.BusinessException;
import com.reward.common.response.ApiResponse;
import com.reward.common.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 观众服务
 */
@Service
public class AudienceService {

    private static final Logger logger = LoggerFactory.getLogger(AudienceService.class);

    @Autowired
    private AudienceMapper audienceMapper;

    @Autowired
    private AnalyticsServiceClient analyticsServiceClient;

    /**
     * 查询观众标签
     */
    public ApiResponse<Map<String, Object>> getAudienceTag(String audienceId) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("查询观众标签: audienceId={}, traceId={}", audienceId, traceId);

        try {
            // 检查观众是否存在
            if (audienceMapper.existsByAudienceId(audienceId) == 0) {
                throw new BusinessException("观众不存在");
            }

            // 调用经营分析服务获取标签，带超时控制
            Map<String, Object> tag = analyticsServiceClient.getAudienceTag(audienceId);

            logger.info("查询观众标签成功: audienceId={}", audienceId);
            return ApiResponse.success(tag);

        } catch (BusinessException e) {
            logger.error("查询观众标签业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("查询观众标签失败: audienceId={}, error={}", audienceId, e.getMessage(), e);
            // 降级处理：返回友好提示
            return ApiResponse.success(Map.of(
                    "tagLevel", "unknown",
                    "message", "标签服务暂时不可用，请稍后重试"
            ));
        }
    }

    /**
     * 查询观众信息
     */
    public ApiResponse<Audience> getAudienceInfo(String audienceId) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("查询观众信息: audienceId={}, traceId={}", audienceId, traceId);

        try {
            Audience audience = audienceMapper.selectByAudienceId(audienceId);
            if (audience == null) {
                throw new BusinessException("观众不存在");
            }

            return ApiResponse.success(audience);

        } catch (BusinessException e) {
            logger.error("查询观众信息业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("查询观众信息失败: audienceId={}, error={}", audienceId, e.getMessage(), e);
            throw new BusinessException("查询失败，请稍后重试");
        }
    }
}