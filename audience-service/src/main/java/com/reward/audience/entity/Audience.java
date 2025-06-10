package com.reward.audience.entity;

import java.sql.Timestamp;

/**
 * 观众信息实体类
 */
public class Audience {

    private Long id;
    private String audienceId;    // 观众ID
    private String nickname;      // 昵称
    private Integer gender;       // 性别 1-男 2-女
    private Timestamp createTime; // 创建时间
    private Timestamp updateTime; // 更新时间

    // 构造方法
    public Audience() {}

    public Audience(String audienceId, String nickname, Integer gender) {
        this.audienceId = audienceId;
        this.nickname = nickname;
        this.gender = gender;
    }

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAudienceId() { return audienceId; }
    public void setAudienceId(String audienceId) { this.audienceId = audienceId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }

    @Override
    public String toString() {
        return "Audience{" +
                "id=" + id +
                ", audienceId='" + audienceId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender=" + gender +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}