package com.reward.analytics.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RewardDataSync {
    private Long id;
    private String recordId;
    private String audienceId;
    private String anchorId;
    private BigDecimal amount;
    private Integer gender;
    private Date rewardTime;
    private Date syncTime;
    private Integer processed;
    private String traceId;
} 