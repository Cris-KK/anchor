package com.reward.audience.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 打赏记录实体类
 */
public class RewardRecord {

    private Long id;
    private String recordId;           // 打赏记录ID(业务幂等性)
    private String audienceId;         // 观众ID
    private String anchorId;           // 主播ID
    private BigDecimal amount;         // 打赏金额
    private Timestamp rewardTime;      // 打赏时间
    private Integer syncStatus;        // 同步状态 0-未同步 1-已同步 2-同步失败
    private Integer syncRetryCount;    // 同步重试次数
    private String traceId;           // 链路追踪ID
    private Timestamp createTime;      // 创建时间
    private Timestamp updateTime;      // 更新时间

    // 构造方法
    public RewardRecord() {}

    public RewardRecord(String recordId, String audienceId, String anchorId, BigDecimal amount) {
        this.recordId = recordId;
        this.audienceId = audienceId;
        this.anchorId = anchorId;
        this.amount = amount;
        this.syncStatus = 0;
        this.syncRetryCount = 0;
        this.rewardTime = new Timestamp(System.currentTimeMillis());
    }

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getAudienceId() { return audienceId; }
    public void setAudienceId(String audienceId) { this.audienceId = audienceId; }

    public String getAnchorId() { return anchorId; }
    public void setAnchorId(String anchorId) { this.anchorId = anchorId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Timestamp getRewardTime() { return rewardTime; }
    public void setRewardTime(Timestamp rewardTime) { this.rewardTime = rewardTime; }

    public Integer getSyncStatus() { return syncStatus; }
    public void setSyncStatus(Integer syncStatus) { this.syncStatus = syncStatus; }

    public Integer getSyncRetryCount() { return syncRetryCount; }
    public void setSyncRetryCount(Integer syncRetryCount) { this.syncRetryCount = syncRetryCount; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }

    @Override
    public String toString() {
        return "RewardRecord{" +
                "id=" + id +
                ", recordId='" + recordId + '\'' +
                ", audienceId='" + audienceId + '\'' +
                ", anchorId='" + anchorId + '\'' +
                ", amount=" + amount +
                ", rewardTime=" + rewardTime +
                ", syncStatus=" + syncStatus +
                ", syncRetryCount=" + syncRetryCount +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}