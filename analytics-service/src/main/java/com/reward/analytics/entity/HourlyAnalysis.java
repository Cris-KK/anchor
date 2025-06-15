package com.reward.analytics.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class HourlyAnalysis {
    private Long id;
    private String anchorId;
    private Integer gender;
    private String hourTime;
    private Date dateTime;
    private BigDecimal totalAmount;
    private Integer totalCount;
    private Date createTime;
    private Date updateTime;
} 