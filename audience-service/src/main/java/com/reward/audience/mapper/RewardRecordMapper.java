package com.reward.audience.mapper;

import com.reward.audience.entity.RewardRecord;
import com.reward.audience.entity.TopAudienceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 打赏记录Mapper接口
 */
@Mapper
public interface RewardRecordMapper {

    /**
     * 插入打赏记录
     */
    int insert(RewardRecord record);

    /**
     * 根据recordId查询
     */
    RewardRecord selectByRecordId(@Param("recordId") String recordId);

    /**
     * 根据主播ID查询TOP10观众
     */
    List<TopAudienceVO> selectTop10ByAnchorId(@Param("anchorId") String anchorId);

    /**
     * 更新同步状态
     */
    int updateSyncStatus(@Param("recordId") String recordId, @Param("syncStatus") Integer syncStatus);

    /**
     * 更新同步状态和重试次数
     */
    int updateSyncStatusWithRetry(@Param("recordId") String recordId,
                                  @Param("syncStatus") Integer syncStatus,
                                  @Param("retryCount") Integer retryCount);

    /**
     * 查询同步失败的记录
     */
    List<RewardRecord> findFailedSyncRecords(@Param("maxRetryCount") int maxRetryCount,
                                             @Param("limit") int limit);

    /**
     * 查询未同步的记录
     */
    List<RewardRecord> findUnsyncedRecords(@Param("offset") int offset,
                                           @Param("limit") int limit);

    /**
     * 统计观众打赏总金额
     */
    Object getAudienceStats(@Param("audienceId") String audienceId);

    /**
     * 批量查询打赏记录
     */
    List<RewardRecord> selectByCondition(@Param("audienceId") String audienceId,
                                         @Param("anchorId") String anchorId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
}