package com.reward.audience.config;

import com.reward.common.config.TraceIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 */
@Configuration
@Import({com.reward.common.config.RestTemplateConfig.class,
        com.reward.common.config.GlobalExceptionHandler.class})
public class WebConfig implements WebMvcConfigurer {


}