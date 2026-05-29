package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Line;

import java.time.LocalDate;

/**
 * 产线服务接口
 */
public interface LineService {

    /**
     * 添加产线
     */
    Result addLine(Line line);

    /**
     * 根据ID删除产线
     */
    Result deleteLine(Long id);

    /**
     * 更新产线
     */
    Result updateLine(Line line);

    /**
     * 根据ID查询产线
     */
    Result findLineById(Long id);

    /**
     * 查询所有产线（分页）
     */
    Result findAll();

    /**
     * 统计每条产线在指定日期范围内的计划数量和完成数量
     */
    Result statistics(LocalDate startTime, LocalDate endTime);
}
