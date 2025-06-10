package com.reward.audience.entity;

import java.sql.Timestamp;

/**
 * 主播信息实体类
 */
public class Anchor {

    private Long id;
    private String anchorId;      // 主播ID
    private String nickname;      // 昵称
    private Integer status;       // 状态 1-正常 0-禁用
    private Timestamp createTime; // 创建时间
    private Timestamp updateTime; // 更新时间

    // 构造方法
    public Anchor() {}

    public Anchor(String anchorId, String nickname, Integer status) {
        this.anchorId = anchorId;
        this.nickname = nickname;
        this.status = status;
    }

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAnchorId() { return anchorId; }
    public void setAnchorId(String anchorId) { this.anchorId = anchorId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }

    @Override
    public String toString() {
        return "Anchor{" +
                "id=" + id +
                ", anchorId='" + anchorId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}