package com.reward.audience.controller;

import com.reward.audience.entity.TopAudienceVO;
import com.reward.audience.service.RewardService;
import com.reward.common.dto.RewardRequest;
import com.reward.common.response.ApiResponse;
import com.reward.common.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 打赏相关接口控制器
 */
@RestController
@RequestMapping("/audience")
public class RewardController {

    private static final Logger logger = LoggerFactory.getLogger(RewardController.class);

    @Autowired
    private RewardService rewardService;

    /**
     * 观众打赏接口
     */
    @PostMapping("/reward")
    public ApiResponse<Void> reward(@Valid @RequestBody RewardRequest request,
                                    HttpServletRequest httpRequest) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("观众打赏请求: request={}, ip={}, traceId={}",
                request, httpRequest.getRemoteAddr(), traceId);

        return rewardService.reward(request);
    }

    /**
     * 查询主播TOP10观众
     */
    @GetMapping("/reward/top10/{anchorId}")
    public ApiResponse<List<TopAudienceVO>> getTop10Audiences(@PathVariable String anchorId) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("查询主播TOP10观众: anchorId={}, traceId={}", anchorId, traceId);

        return rewardService.getTop10Audiences(anchorId);
    }

    /**
     * 查询观众统计信息
     */
    @GetMapping("/{audienceId}/stats")
    public ApiResponse<Object> getAudienceStats(@PathVariable String audienceId) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("查询观众统计: audienceId={}, traceId={}", audienceId, traceId);

        return rewardService.getAudienceStats(audienceId);
    }
}