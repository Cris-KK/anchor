package com.reward.common.config;

import com.reward.common.util.TraceIdUtil;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TraceId过滤器 - 用于链路追踪
 */
@Component
@Order(1)
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_HEADER = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 从请求头获取TraceId，如果没有则生成新的
            String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
            if (traceId == null || traceId.trim().isEmpty()) {
                traceId = TraceIdUtil.getTraceId();
            } else {
                TraceIdUtil.setTraceId(traceId);
            }

            // 设置到MDC中，用于日志输出
            MDC.put("traceId", traceId);

            // 设置响应头
            httpResponse.setHeader(TRACE_ID_HEADER, traceId);

            // 继续处理请求
            chain.doFilter(request, response);

        } finally {
            // 清理ThreadLocal和MDC
            TraceIdUtil.clear();
            MDC.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化逻辑（如果需要）
    }

    @Override
    public void destroy() {
        // 清理逻辑（如果需要）
    }
}