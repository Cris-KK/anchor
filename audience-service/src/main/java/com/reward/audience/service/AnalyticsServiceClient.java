package com.reward.audience.service;

import com.reward.common.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 经营分析服务客户端
 */
@Service
public class AnalyticsServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.analytics.url}")
    private String analyticsServiceUrl;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * 查询观众标签（带超时控制）
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAudienceTag(String audienceId) {
        String traceId = TraceIdUtil.getTraceId();

        try {
            String url = analyticsServiceUrl + "/analytics/audience/" + audienceId + "/tag";

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("traceId", traceId);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // 使用线程池执行带超时的请求
            Future<Map<String, Object>> future = executor.submit(() -> {
                ResponseEntity<Map> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, Map.class);

                Map<String, Object> body = response.getBody();
                if (body != null && "200".equals(String.valueOf(body.get("code")))) {
                    return (Map<String, Object>) body.get("data");
                } else {
                    throw new RuntimeException("分析服务返回错误: " + body);
                }
            });

            // 2秒超时
            return future.get(2, TimeUnit.SECONDS);

        } catch (TimeoutException e) {
            logger.warn("查询观众标签超时: audienceId={}, traceId={}", audienceId, traceId);
            throw new RuntimeException("标签服务响应超时");
        } catch (Exception e) {
            logger.error("查询观众标签失败: audienceId={}, error={}, traceId={}",
                    audienceId, e.getMessage(), traceId);
            throw new RuntimeException("标签服务不可用");
        }
    }
}