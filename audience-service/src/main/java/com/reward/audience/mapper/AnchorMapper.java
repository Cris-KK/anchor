package com.reward.audience.mapper;

import com.reward.audience.entity.Anchor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 主播信息Mapper接口
 */
@Mapper
public interface AnchorMapper {

    /**
     * 根据主播ID查询主播信息
     */
    Anchor selectByAnchorId(@Param("anchorId") String anchorId);

    /**
     * 插入主播信息
     */
    int insert(Anchor anchor);

    /**
     * 更新主播信息
     */
    int update(Anchor anchor);

    /**
     * 检查主播是否存在且状态正常
     */
    int existsActiveAnchor(@Param("anchorId") String anchorId);
}