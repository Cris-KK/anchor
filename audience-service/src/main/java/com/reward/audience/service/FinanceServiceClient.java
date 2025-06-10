package com.reward.audience.service;

import com.reward.common.util.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 财务服务客户端
 */
@Service
public class FinanceServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(FinanceServiceClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.finance.url}")
    private String financeServiceUrl;

    /**
     * 发送结算请求
     */
    public void sendSettlementRequest(Map<String, Object> settlementData) {
        String traceId = TraceIdUtil.getTraceId();

        try {
            String url = financeServiceUrl + "/finance/settlement";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("traceId", traceId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(settlementData, headers);

            restTemplate.postForObject(url, entity, String.class);

            logger.debug("财务结算请求发送成功: recordId={}", settlementData.get("recordId"));

        } catch (Exception e) {
            logger.error("财务结算请求发送失败: recordId={}, error={}",
                    settlementData.get("recordId"), e.getMessage());
            throw e;
        }
    }
}