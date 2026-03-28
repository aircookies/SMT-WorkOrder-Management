package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.entity.Line;
import org.apache.ibatis.annotations.Mapper;

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
}
