// ==================== 1. 统一响应格式类 ====================
// common-utils/src/main/java/com/reward/common/response/ApiResponse.java
package com.reward.common.response;

import com.reward.common.util.TraceIdUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一API响应格式
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private String traceId;
    private Long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
        this.traceId = TraceIdUtil.getTraceId();
    }

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return success(null, "操作成功");
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "操作成功");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 业务异常响应
     */
    public static <T> ApiResponse<T> fail(String message) {
        return error(400, message);
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", traceId='" + traceId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

