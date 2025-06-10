package com.reward.common.dto;




import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 打赏请求DTO
 */
public class RewardRequest {

    @NotBlank(message = "观众ID不能为空")
    private String audienceId;

    @NotBlank(message = "主播ID不能为空")
    private String anchorId;

    @NotNull(message = "打赏金额不能为空")
    @DecimalMin(value = "0.01", message = "打赏金额必须大于0")
    private BigDecimal amount;

    private String audienceNickname;

    private Integer gender; // 1-男 2-女

    // 构造函数
    public RewardRequest() {}

    public RewardRequest(String audienceId, String anchorId, BigDecimal amount,
                         String audienceNickname, Integer gender) {
        this.audienceId = audienceId;
        this.anchorId = anchorId;
        this.amount = amount;
        this.audienceNickname = audienceNickname;
        this.gender = gender;
    }

    // Getters and Setters
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

    public String getAudienceNickname() {
        return audienceNickname;
    }

    public void setAudienceNickname(String audienceNickname) {
        this.audienceNickname = audienceNickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "RewardRequest{" +
                "audienceId='" + audienceId + '\'' +
                ", anchorId='" + anchorId + '\'' +
                ", amount=" + amount +
                ", audienceNickname='" + audienceNickname + '\'' +
                ", gender=" + gender +
                '}';
    }
}

