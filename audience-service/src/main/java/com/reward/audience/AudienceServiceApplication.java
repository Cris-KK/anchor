package com.reward.audience;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.reward.audience.mapper")
@ComponentScan(basePackages = {"com.reward.audience", "com.reward.common"})
@EnableAsync
@EnableScheduling
public class AudienceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudienceServiceApplication.class, args);

        System.out.println("==========================================");
        System.out.println("观众服务启动成功!");
        System.out.println("访问地址: http://localhost:8091");
        System.out.println("健康检查: http://localhost:8091/health");
        System.out.println("==========================================");
    }
}