package com.reward.audience.entity;

import java.math.BigDecimal;

/**
 * TOP观众VO类
 */
public class TopAudienceVO {

    private String audienceId;        // 观众ID
    private String nickname;          // 观众昵称
    private BigDecimal totalAmount;   // 总打赏金额
    private Integer rewardCount;      // 打赏次数
    private Integer ranking;          // 排名

    // 构造方法
    public TopAudienceVO() {}

    public TopAudienceVO(String audienceId, String nickname, BigDecimal totalAmount,
                         Integer rewardCount, Integer ranking) {
        this.audienceId = audienceId;
        this.nickname = nickname;
        this.totalAmount = totalAmount;
        this.rewardCount = rewardCount;
        this.ranking = ranking;
    }

    // Getter and Setter methods
    public String getAudienceId() { return audienceId; }
    public void setAudienceId(String audienceId) { this.audienceId = audienceId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Integer getRewardCount() { return rewardCount; }
    public void setRewardCount(Integer rewardCount) { this.rewardCount = rewardCount; }

    public Integer getRanking() { return ranking; }
    public void setRanking(Integer ranking) { this.ranking = ranking; }

    @Override
    public String toString() {
        return "TopAudienceVO{" +
                "audienceId='" + audienceId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", totalAmount=" + totalAmount +
                ", rewardCount=" + rewardCount +
                ", ranking=" + ranking +
                '}';
    }
}