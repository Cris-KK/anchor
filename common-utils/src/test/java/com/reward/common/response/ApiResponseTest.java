package com.reward.common.response;// com.reward.common.response.ApiResponseTest.java

import com.reward.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApiResponseTest {

    @Test
    public void testSuccessResponse() {
        String data = "test data";
        ApiResponse<String> response = ApiResponse.success(data);

        assertEquals(200, response.getCode());
        assertEquals("操作成功", response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTraceId());
        assertNotNull(response.getTimestamp());
    }

    @Test
    public void testErrorResponse() {
        String errorMessage = "test error";
        ApiResponse<Void> response = ApiResponse.error(errorMessage);

        assertEquals(500, response.getCode());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTraceId());
    }

    @Test
    public void testFailResponse() {
        String failMessage = "business fail";
        ApiResponse<Void> response = ApiResponse.fail(failMessage);

        assertEquals(400, response.getCode());
        assertEquals(failMessage, response.getMessage());
    }
}