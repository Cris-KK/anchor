package com.reward.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RewardSyncDTO {
    private String recordId;
    private String audienceId;
    private String anchorId;
    private BigDecimal amount;
    private Integer gender;
    private Date rewardTime;
    private String traceId;
} 