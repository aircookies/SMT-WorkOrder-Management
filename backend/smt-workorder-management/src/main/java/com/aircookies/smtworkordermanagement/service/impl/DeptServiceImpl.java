package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;
import com.aircookies.smtworkordermanagement.mapper.DeptMapper;
import com.aircookies.smtworkordermanagement.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeptServiceImpl implements DeptService {
    private final DeptMapper deptMapper;

    @Autowired
    public DeptServiceImpl(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    @Override
    public Result addDept(Dept dept) {
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.addDept(dept);
        return Result.success("添加部门成功");
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
