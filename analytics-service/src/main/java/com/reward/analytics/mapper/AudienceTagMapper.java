package com.reward.analytics.mapper;

import com.reward.analytics.entity.AudienceTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AudienceTagMapper {
    
    /**
     * 根据观众ID查询标签
     */
    AudienceTag findByAudienceId(@Param("audienceId") String audienceId);
    
    /**
     * 插入观众标签
     */
    void insert(AudienceTag tag);
    
    /**
     * 更新观众标签
     */
    void update(AudienceTag tag);
    
    /**
     * 更新标签等级和排名
     */
    void updateTagLevel(AudienceTag tag);
    
    /**
     * 查询所有观众标签
     */
    List<AudienceTag> findAll();
    
    /**
     * 批量更新观众标签
     */
    void batchUpdate(@Param("list") List<AudienceTag> list);
    
    /**
     * 更新最后计算时间
     */
    void updateLastCalcTime(@Param("audienceId") String audienceId);
} 