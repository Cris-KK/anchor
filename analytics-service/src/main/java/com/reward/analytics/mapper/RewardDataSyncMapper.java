package com.reward.analytics.mapper;

import com.reward.analytics.entity.RewardDataSync;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

@Mapper
public interface RewardDataSyncMapper {
    
    /**
     * 插入同步数据
     */
    void insert(RewardDataSync sync);
    
    /**
     * 查询未处理的同步数据
     */
    List<RewardDataSync> findUnprocessed(@Param("limit") int limit);
    
    /**
     * 更新处理状态
     */
    void updateProcessed(@Param("recordId") String recordId);
    
    /**
     * 根据观众ID查询打赏记录
     */
    List<RewardDataSync> findByAudienceId(@Param("audienceId") String audienceId);
    
    /**
     * 根据时间范围查询打赏记录
     */
    List<RewardDataSync> findByTimeRange(
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime
    );
} 