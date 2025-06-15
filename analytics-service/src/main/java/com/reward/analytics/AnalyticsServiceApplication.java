package com.reward.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class AnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
        System.out.println("==========================================");
        System.out.println("观众分析启动成功!");
        System.out.println("访问地址: http://localhost:8093");
        System.out.println("==========================================");
    }
} 