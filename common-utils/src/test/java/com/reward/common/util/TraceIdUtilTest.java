package com.reward.common.util;// com.reward.common.util.TraceIdUtilTest.java

import com.reward.common.util.TraceIdUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class TraceIdUtilTest {

    @AfterEach
    public void cleanup() {
        TraceIdUtil.clear();
    }

    @Test
    public void testGenerateTraceId() {
        String traceId1 = TraceIdUtil.getTraceId();
        String traceId2 = TraceIdUtil.getTraceId();

        assertNotNull(traceId1);
        assertNotNull(traceId2);
        assertEquals(traceId1, traceId2); // 同一线程内应该相同
        assertEquals(32, traceId1.length()); // UUID去掉"-"后长度为32
    }

    @Test
    public void testSetAndGetTraceId() {
        String customTraceId = "test-trace-id-123";
        TraceIdUtil.setTraceId(customTraceId);

        String retrievedTraceId = TraceIdUtil.getTraceId();
        assertEquals(customTraceId, retrievedTraceId);
    }

    @Test
    public void testClearTraceId() {
        String traceId1 = TraceIdUtil.getTraceId();
        TraceIdUtil.clear();
        String traceId2 = TraceIdUtil.getTraceId();

        assertNotEquals(traceId1, traceId2);
    }
}