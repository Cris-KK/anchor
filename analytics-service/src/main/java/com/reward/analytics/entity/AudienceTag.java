package com.reward.analytics.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AudienceTag {
    private Long id;
    private String audienceId;
    private String tagLevel;
    private BigDecimal totalAmount;
    private BigDecimal rankPercentage;
    private Date lastCalcTime;
    private Date createTime;
} 