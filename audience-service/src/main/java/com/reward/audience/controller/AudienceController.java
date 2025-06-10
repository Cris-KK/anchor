package com.reward.audience.controller;

import com.reward.audience.entity.Audience;
import com.reward.audience.service.AudienceService;
import com.reward.common.response.ApiResponse;
import com.reward.common.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 观众信息相关接口控制器
 */
@RestController
@RequestMapping("/audience")
public class AudienceController {

    private static final Logger logger = LoggerFactory.getLogger(AudienceController.class);

    @Autowired
    private AudienceService audienceService;

    /**
     * 查询观众标签
     */
    @GetMapping("/{audienceId}/tag")
    public ApiResponse<Map<String, Object>> getAudienceTag(@PathVariable String audienceId) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("查询观众标签: audienceId={}, traceId={}", audienceId, traceId);

        return audienceService.getAudienceTag(audienceId);
    }

    /**
     * 查询观众信息
     */
    @GetMapping("/{audienceId}/info")
    public ApiResponse<Audience> getAudienceInfo(@PathVariable String audienceId) {
        String traceId = TraceIdUtil.getTraceId();
        logger.info("查询观众信息: audienceId={}, traceId={}", audienceId, traceId);

        return audienceService.getAudienceInfo(audienceId);
    }
}