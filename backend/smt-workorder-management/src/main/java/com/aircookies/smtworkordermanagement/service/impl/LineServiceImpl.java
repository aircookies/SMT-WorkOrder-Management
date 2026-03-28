package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.entity.Line;
import com.aircookies.smtworkordermanagement.mapper.LineMapper;
import com.aircookies.smtworkordermanagement.service.LineService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.aircookies.smtworkordermanagement.common.Result.success;

/**
 * 产线服务实现类
 */
@Service
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
        line.setCreateTime(LocalDateTime.now());
        line.setUpdateTime(LocalDateTime.now());
        int res = lineMapper.addLine(line);
        if (res != 0) {
            return success("添加产线成功");
        } else {
            return Result.error(1, "添加产线失败");
        }
    }

    /**
     * 根据ID删除产线
     */
    @Override
    public Result deleteLine(Long id) {
        lineMapper.deleteLine(id);
        return Result.success("删除产线成功");
    }

    /**
     * 更新产线
     */
    @Override
    public Result updateLine(Line line) {
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
        return success(line);
    }

    /**
     * 查询所有产线
     */
    @Override
    public Result findAll() {
        // 查询所有产线
        return success(lineMapper.findAll());
    }
}
