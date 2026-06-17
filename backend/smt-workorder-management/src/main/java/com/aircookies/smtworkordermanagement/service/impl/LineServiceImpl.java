package com.aircookies.smtworkordermanagement.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import static com.aircookies.smtworkordermanagement.common.Result.success;
import com.aircookies.smtworkordermanagement.entity.Line;
import com.aircookies.smtworkordermanagement.mapper.LineMapper;
import com.aircookies.smtworkordermanagement.service.LineService;

import lombok.extern.slf4j.Slf4j;

/**
 * 产线服务实现类
 * <p>
 * 实现产线管理的核心业务逻辑，包括增删改查和产量统计。
 * 删除产线前会检查是否有工单正在使用该产线，防止数据不一致。
 * 查询结果通过 Spring Cache 自动缓存（过期时间 24 小时）。
 * </p>
 */
@Service
@Slf4j
public class LineServiceImpl implements LineService {

    private final LineMapper lineMapper;

    @Autowired
    public LineServiceImpl(LineMapper lineMapper) {
        this.lineMapper = lineMapper;
    }

    /**
     * 添加产线
     */
    @Override
    public Result addLine(Line line) {
        // 判断产线是否已存在
        if (lineMapper.findLineByName(line.getName()) != null) {
            log.info("尝试添加已存在的产线 {}，失败", line.getName());
            throw new BusinessException("该产线已存在");
        }

        line.setCreateTime(LocalDateTime.now());
        line.setUpdateTime(LocalDateTime.now());
        int res = lineMapper.addLine(line);
        if (res != 0) {
            return success("添加产线成功");
        } else {
            log.info("添加产线 {} 失败", line.getName());
            throw new BusinessException("添加产线失败");
        }
    }

    /**
     * 根据ID删除产线
     */
    @Override
    public Result deleteLine(Long id) {
        if (lineMapper.isLineUsed(id) != 0) {
            log.info("尝试删除正在使用的中的产线 {}，失败", id);
            throw new BusinessException("该产线正在使用中，请先删除该产线下的工单");
        }

        lineMapper.deleteLine(id);
        return Result.success("删除产线成功");
    }

    /**
     * 更新产线
     */
    @Override
    public Result updateLine(Line line) {
        // 判断产线是否已存在
        if (line == null || lineMapper.findLineById(line.getId()) == null) {
            log.info("尝试更新不存在存在的产线，失败");
            throw new BusinessException("该产线不存在");
        }

        line.setUpdateTime(LocalDateTime.now());
        lineMapper.updateLine(line);
        return Result.success("更新产线成功");
    }

    /**
     * 根据ID查询产线
     */
    @Override
    public Result findLineById(Long id) {
        Line line = lineMapper.findLineById(id);
        return Result.success(line);
    }

    /**
     * 查询所有产线
     */
    @Override
    public Result findAll() {
        // 查询所有产线
        return Result.success(lineMapper.findAll());
    }

    /**
     * 统计每条产线在指定日期范围内的计划数量和完成数量
     */
    @Override
    public Result statistics(LocalDate startTime, LocalDate endTime) {
        return Result.success(lineMapper.statistics(startTime, endTime));
    }
}