package com.reward.audience.service;

import com.reward.audience.entity.Audience;
import com.reward.audience.entity.Anchor;
import com.reward.audience.entity.RewardRecord;
import com.reward.audience.entity.TopAudienceVO;
import com.reward.audience.mapper.AudienceMapper;
import com.reward.audience.mapper.AnchorMapper;
import com.reward.audience.mapper.RewardRecordMapper;
import com.reward.common.dto.RewardRequest;
import com.reward.common.exception.BusinessException;
import com.reward.common.response.ApiResponse;
import com.reward.common.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 打赏服务
 */
@Service
public class RewardService {

    private static final Logger logger = LoggerFactory.getLogger(RewardService.class);

    @Autowired
    private RewardRecordMapper rewardRecordMapper;

    @Autowired
    private AudienceMapper audienceMapper;

    @Autowired
    private AnchorMapper anchorMapper;

    @Autowired
    private DataSyncService dataSyncService;

    @Value("${audience.reward.max-amount:10000}")
    private BigDecimal maxRewardAmount;

    /**
     * 处理打赏请求
     */
    @Transactional
    public ApiResponse<Void> reward(RewardRequest request) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("处理打赏请求: request={}, traceId={}", request, traceId);

        try {
            // 1. 参数验证
            validateRewardRequest(request);

            // 2. 检查用户和主播是否存在
            checkUsersExist(request.getAudienceId(), request.getAnchorId());

            // 3. 生成唯一的记录ID
            String recordId = generateRecordId(request);

            // 4. 检查是否重复提交
            if (rewardRecordMapper.selectByRecordId(recordId) != null) {
                logger.warn("重复提交的打赏请求: recordId={}", recordId);
                return ApiResponse.success(); // 幂等性处理
            }

            // 5. 创建打赏记录
            RewardRecord record = new RewardRecord(recordId, request.getAudienceId(),
                    request.getAnchorId(), request.getAmount());
            record.setTraceId(traceId);

            // 6. 保存到数据库
            int result = rewardRecordMapper.insert(record);
            if (result <= 0) {
                throw new BusinessException("打赏记录保存失败");
            }

            // 7. 异步同步到其他服务
            dataSyncService.syncRewardRecord(record);

            logger.info("打赏处理成功: recordId={}, audienceId={}, anchorId={}, amount={}",
                    recordId, request.getAudienceId(), request.getAnchorId(), request.getAmount());

            return ApiResponse.success();

        } catch (BusinessException e) {
            logger.error("打赏业务异常: {}, traceId={}", e.getMessage(), traceId);
            throw e;
        } catch (Exception e) {
            logger.error("打赏处理失败: {}, traceId={}", e.getMessage(), traceId, e);
            throw new BusinessException("打赏处理失败，请稍后重试");
        }
    }

    /**
     * 查询主播TOP10观众
     */
    public ApiResponse<List<TopAudienceVO>> getTop10Audiences(String anchorId) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("查询主播TOP10观众: anchorId={}, traceId={}", anchorId, traceId);

        try {
            // 检查主播是否存在
            if (anchorMapper.existsActiveAnchor(anchorId) == 0) {
                throw new BusinessException("主播不存在或已被禁用");
            }

            List<TopAudienceVO> topAudiences = rewardRecordMapper.selectTop10ByAnchorId(anchorId);

            logger.info("查询TOP10观众成功: anchorId={}, count={}", anchorId, topAudiences.size());
            return ApiResponse.success(topAudiences);

        } catch (BusinessException e) {
            logger.error("查询TOP10观众业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("查询TOP10观众失败: anchorId={}, error={}", anchorId, e.getMessage(), e);
            throw new BusinessException("查询失败，请稍后重试");
        }
    }

    /**
     * 获取观众统计信息
     */
    public ApiResponse<Object> getAudienceStats(String audienceId) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("查询观众统计: audienceId={}, traceId={}", audienceId, traceId);

        try {
            // 检查观众是否存在
            if (audienceMapper.existsByAudienceId(audienceId) == 0) {
                throw new BusinessException("观众不存在");
            }

            Object stats = rewardRecordMapper.getAudienceStats(audienceId);
            return ApiResponse.success(stats);

        } catch (BusinessException e) {
            logger.error("查询观众统计业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("查询观众统计失败: audienceId={}, error={}", audienceId, e.getMessage(), e);
            throw new BusinessException("查询失败，请稍后重试");
        }
    }

    /**
     * 验证打赏请求参数
     */
    private void validateRewardRequest(RewardRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }

        if (request.getAudienceId() == null || request.getAudienceId().trim().isEmpty()) {
            throw new BusinessException("观众ID不能为空");
        }

        if (request.getAnchorId() == null || request.getAnchorId().trim().isEmpty()) {
            throw new BusinessException("主播ID不能为空");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("打赏金额必须大于0");
        }

        if (request.getAmount().compareTo(maxRewardAmount) > 0) {
            throw new BusinessException("打赏金额不能超过" + maxRewardAmount + "元");
        }
    }

    /**
     * 检查用户和主播是否存在
     */
    private void checkUsersExist(String audienceId, String anchorId) {
        // 检查观众是否存在
        if (audienceMapper.existsByAudienceId(audienceId) == 0) {
            throw new BusinessException("观众不存在");
        }

        // 检查主播是否存在且状态正常
        if (anchorMapper.existsActiveAnchor(anchorId) == 0) {
            throw new BusinessException("主播不存在或已被禁用");
        }
    }

    /**
     * 生成唯一的记录ID
     */
    private String generateRecordId(RewardRequest request) {
        // 使用时间戳 + UUID的方式生成唯一ID
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "RWD" + timestamp + uuid.substring(0, 8);
    }
}