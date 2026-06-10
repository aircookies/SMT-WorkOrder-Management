package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.dto.LineOutputDTO;
import com.aircookies.smtworkordermanagement.entity.Line;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 产线数据访问层
 */
@Mapper
public interface LineMapper {

    /**
     * 添加产线
     */
    int addLine(Line line);

    /**
     * 根据ID删除产线
     */
    void deleteLine(Long id);

    /**
     * 更新产线
     */
    void updateLine(Line line);

    /**
     * 根据ID查询产线
     */
    Line findLineById(Long id);

    /**
     * 查询所有产线
     */
    List<Line> findAll();

    /**
     * 统计每条产线在指定日期范围内的计划数量和完成数量
     */
    List<LineOutputDTO> statistics(LocalDate startTime, LocalDate endTime);

    /**
     * 查询产线是否被使用
     */
    int isLineUsed(Long id);
}
