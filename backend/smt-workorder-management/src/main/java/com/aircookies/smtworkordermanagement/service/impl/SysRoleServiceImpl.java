package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.SysRole;
import com.aircookies.smtworkordermanagement.mapper.SysRoleMapper;
import com.aircookies.smtworkordermanagement.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {
    private final SysRoleMapper sysRoleMapper;

    @Autowired
    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    // 添加角色
    @Override
    public Result addRole(SysRole sysRole) {
        sysRole.setCreateTime(LocalDateTime.now());
        sysRole.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.addRole(sysRole);
        return Result.success("添加角色成功");
    }

    // 更新角色
    @Override
    public Result updateRole(SysRole sysRole) {
        sysRole.setUpdateTime(LocalDateTime.now());
        sysRoleMapper.updateRole(sysRole);
        return Result.success("更新角色成功");
    }

    // 删除角色
    @Override
    public Result deleteRole(Integer id) {
        sysRoleMapper.deleteRole(id);
        return Result.success("删除角色成功");
    }

    // 根据ID查询角色
    @Override
    public Result findById(Integer id) {
        SysRole role = sysRoleMapper.findById(id);
        return Result.success(role);
    }

    // 查询所有角色
    @Override
    public Result findAll() {
        List<SysRole> roles = sysRoleMapper.findAll();
        return Result.success(roles);
    }

    // 条件查询角色
    @Override
    public Result findRoles(SysRole sysRole) {
        List<SysRole> roles = sysRoleMapper.findRoles(sysRole);
        return Result.success(roles);
    }
}