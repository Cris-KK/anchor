package com.reward.audience.controller;

import com.reward.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
public class HealthController {

    @Value("${server.port:8091}")
    private String serverPort;

    @Value("${spring.application.name:audience-service}")
    private String serviceName;

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", serviceName);
        healthInfo.put("port", serverPort);
        healthInfo.put("timestamp", System.currentTimeMillis());
        healthInfo.put("version", "1.0.0");

        return ApiResponse.success(healthInfo);
    }

    /**
     * 服务信息接口
     */
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("serviceName", serviceName);
        info.put("version", "1.0.0");
        info.put("description", "观众服务 - 处理打赏、查询TOP10观众、观众标签查询");
        info.put("features", new String[]{
                "打赏处理", "TOP10查询", "观众标签查询", "数据同步", "容错处理"
        });

        return ApiResponse.success(info);
    }
}