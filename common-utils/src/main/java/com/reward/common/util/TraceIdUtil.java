package com.reward.common.util;


import java.util.UUID;

/**
 * 链路追踪ID工具类
 */
public class TraceIdUtil {

    private static final ThreadLocal<String> TRACE_ID_HOLDER = new ThreadLocal<>();

    /**
     * 设置TraceId
     */
    public static void setTraceId(String traceId) {
        TRACE_ID_HOLDER.set(traceId);
    }

    /**
     * 获取TraceId，如果不存在则生成新的
     */
    public static String getTraceId() {
        String traceId = TRACE_ID_HOLDER.get();
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
            TRACE_ID_HOLDER.set(traceId);
        }
        return traceId;
    }

    /**
     * 清除TraceId
     */
    public static void clear() {
        TRACE_ID_HOLDER.remove();
    }

    /**
     * 生成新的TraceId
     */
    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
