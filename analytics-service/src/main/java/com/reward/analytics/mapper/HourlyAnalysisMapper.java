package com.reward.analytics.mapper;

import com.reward.analytics.entity.HourlyAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

@Mapper
public interface HourlyAnalysisMapper {
    
    /**
     * 插入小时维度分析数据
     */
    void insert(HourlyAnalysis analysis);
    
    /**
     * 更新小时维度分析数据
     */
    void update(HourlyAnalysis analysis);
    
    /**
     * 根据主播ID、性别、小时和日期查询分析数据
     */
    HourlyAnalysis findByAnchorAndGenderAndHourAndDate(
        @Param("anchorId") String anchorId,
        @Param("gender") Integer gender,
        @Param("hourTime") String hourTime,
        @Param("dateTime") Date dateTime
    );
    
    /**
     * 根据主播ID、性别和时间范围查询分析数据
     */
    List<HourlyAnalysis> findByAnchorAndGenderAndTimeRange(
        @Param("anchorId") String anchorId,
        @Param("gender") Integer gender,
        @Param("startTime") Date startTime,
        @Param("endTime") Date endTime
    );
    
    /**
     * 清空表数据
     */
    void truncate();
    
    /**
     * 批量插入数据
     */
    void batchInsert(@Param("list") List<HourlyAnalysis> list);
} 