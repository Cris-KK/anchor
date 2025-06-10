
package com.reward.common.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务结算请求DTO
 */
public class SettlementRequest {

    private String recordId;
    private String audienceId;
    private String anchorId;
    private BigDecimal amount;
    private LocalDateTime rewardTime;
    private String traceId;

    // 构造函数
    public SettlementRequest() {}

    // Getters and Setters
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getAudienceId() {
        return audienceId;
    }

    public void setAudienceId(String audienceId) {
        this.audienceId = audienceId;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getRewardTime() {
        return rewardTime;
    }

    public void setRewardTime(LocalDateTime rewardTime) {
        this.rewardTime = rewardTime;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "SettlementRequest{" +
                "recordId='" + recordId + '\'' +
                ", audienceId='" + audienceId + '\'' +
                ", anchorId='" + anchorId + '\'' +
                ", amount=" + amount +
                ", rewardTime=" + rewardTime +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}

