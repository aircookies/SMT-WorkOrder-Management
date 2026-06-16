package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;
import com.aircookies.smtworkordermanagement.mapper.DeptMapper;
import com.aircookies.smtworkordermanagement.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 部门服务实现类
 * <p>
 * 实现部门管理的核心业务逻辑，包括增删改查。
 * 添加和修改时自动设置创建/更新时间。
 * 查询结果通过 Spring Cache 注解自动缓存（过期时间 24 小时）。
 * </p>
 */
@Service
@Slf4j
public class DeptServiceImpl implements DeptService {
    private final DeptMapper deptMapper;

    @Autowired
    public DeptServiceImpl(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    @Override
    public Result addDept(Dept dept) {
        if (dept.getName() == null) {
            throw new BusinessException("部门名称不能为空");
        } else if (deptMapper.findDeptByName(dept.getName()) != null) {
            throw new BusinessException("该部门已存在");
        }

        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        int res = deptMapper.addDept(dept);
        if (res != 0) {
            log.info("添加部门成功，部门名称：{}", dept.getName());
            return Result.success("添加部门成功");
        } else {
            log.info("添加部门失败，部门名称：{}", dept.getName());
            throw new BusinessException("添加部门失败");
        }
    }

    @Override
    public Result deleteDept(int id) {
        log.info("删除部门，部门id：{}", id);
        deptMapper.deleteDept(id);
        log.info("删除部门成功，部门id：{}", id);
        return Result.success("删除部门成功");
    }

    @Override
    public Result findDept(Dept dept) {
        return Result.success(deptMapper.findDept(dept));
    }

    @Override
    public Result updateDept(Dept dept) {
        if (dept == null || dept.getName() == null) {
            throw new BusinessException("部门不存在");
        }

        dept.setUpdateTime(LocalDateTime.now());
        int res = deptMapper.updateDept(dept);
        if (res != 0) {
            log.info("更新部门成功，部门名称：{}", dept.getName());
            return Result.success("更新部门成功");
        } else {
            log.info("更新部门失败，部门名称：{}", dept.getName());
            throw new BusinessException("更新部门失败");
        }
    }
}