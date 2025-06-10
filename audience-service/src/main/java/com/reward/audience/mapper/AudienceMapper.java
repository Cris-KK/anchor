package com.reward.audience.mapper;

import com.reward.audience.entity.Audience;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 观众信息Mapper接口
 */
@Mapper
public interface AudienceMapper {

    /**
     * 根据观众ID查询观众信息
     */
    Audience selectByAudienceId(@Param("audienceId") String audienceId);

    /**
     * 插入观众信息
     */
    int insert(Audience audience);

    /**
     * 更新观众信息
     */
    int update(Audience audience);

    /**
     * 检查观众是否存在
     */
    int existsByAudienceId(@Param("audienceId") String audienceId);
}