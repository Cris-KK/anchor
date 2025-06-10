package com.reward.common.config;

import com.reward.common.util.TraceIdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RestTemplate配置类
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // 创建HTTP连接工厂
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 连接超时5秒
        factory.setReadTimeout(10000);   // 读取超时10秒

        // 创建RestTemplate
        RestTemplate restTemplate = new RestTemplate(factory);

        // 添加拦截器传递TraceId
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new TraceIdInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    /**
     * TraceId传递拦截器
     */
    public static class TraceIdInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            // 将当前TraceId添加到请求头
            String traceId = TraceIdUtil.getTraceId();
            if (traceId != null && !traceId.isEmpty()) {
                request.getHeaders().add("traceId", traceId);
            }

            return execution.execute(request, body);
        }
    }
}