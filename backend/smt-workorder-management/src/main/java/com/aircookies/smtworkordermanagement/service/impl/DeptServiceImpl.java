package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;
import com.aircookies.smtworkordermanagement.mapper.DeptMapper;
import com.aircookies.smtworkordermanagement.service.DeptService;
import com.aircookies.smtworkordermanagement.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeptServiceImpl implements DeptService {
    private final DeptMapper deptMapper;
    private final RedisUtil redisUtil;

    @Autowired
    public DeptServiceImpl(DeptMapper deptMapper, RedisUtil redisUtil) {
        this.deptMapper = deptMapper;
        this.redisUtil = redisUtil;
    }

    @Override
    public Result addDept(Dept dept) {
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        int res = deptMapper.addDept(dept);
        if (res != 0) {
            return Result.success("添加部门成功");
        } else {
            throw new BusinessException("添加部门失败");
        }
    }

    @Override
    public Result deleteDept(int id) {
        deptMapper.deleteDept(id);
        return Result.success("删除部门成功");
    }

    @Override
    public Result findDept(Dept dept) {
        return Result.success(deptMapper.findDept(dept));
    }

    @Override
    public Result updateDept(Dept dept) {
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.updateDept(dept);
        return Result.success("更新部门成功");
    }
}
