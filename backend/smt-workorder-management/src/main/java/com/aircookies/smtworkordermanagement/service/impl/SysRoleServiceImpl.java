package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.SysRole;
import com.aircookies.smtworkordermanagement.mapper.SysRoleMapper;
import com.aircookies.smtworkordermanagement.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务实现类
 * <p>
 * 实现系统角色管理（RBAC）的核心业务逻辑，包括增删改查。
 * 角色用于控制用户的功能访问权限，查询结果通过 Spring Cache 自动缓存。
 * </p>
 */
@Service
@Slf4j
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
        int res = sysRoleMapper.addRole(sysRole);
        if (res <= 0) {
            log.error("添加角色失败，角色名称：{}", sysRole.getName());
            return Result.error("添加角色失败");
        } else {
            return Result.success("添加角色成功");
        }
    }

    // 更新角色
    @Override
    public Result updateRole(SysRole sysRole) {
        // 判断角色是否已存在
        if (sysRole == null || sysRoleMapper.findById(sysRole.getId()) == null) {
            log.info("尝试更新不存在存在的角色，失败");
            throw new BusinessException("该角色不存在");
        }

        sysRole.setUpdateTime(LocalDateTime.now());
        int res = sysRoleMapper.updateRole(sysRole);
        if (res <= 0) {
            log.error("更新角色失败，角色名称：{}", sysRole.getName());
            return Result.error("更新角色失败");
        } else {
            return Result.success("更新角色成功");
        }
    }

    // 删除角色
    @Override
    public Result deleteRole(Integer id) {
        int res = sysRoleMapper.deleteRole(id);
        if (res <= 0) {
            log.error("删除角色失败，角色ID：{}", id);
            return Result.error("删除角色失败");
        } else {
            return Result.success("删除角色成功");
        }
    }

    // 根据ID查询角色
    @Override
    public Result findById(Integer id) {
        SysRole role = sysRoleMapper.findById(id);
        if (role == null) {
            return Result.error("角色不存在");
        } else {
            return Result.success(role);
        }
    }

    // 查询所有角色
    @Override
    public Result findAll() {
        List<SysRole> roles = sysRoleMapper.findAll();
        if (roles.isEmpty()) {
            return Result.error("角色列表为空");
        } else {
            return Result.success(roles);
        }
    }

    // 条件查询角色
    @Override
    public Result findRoles(SysRole sysRole) {
        List<SysRole> roles = sysRoleMapper.findRoles(sysRole);
        if (roles.isEmpty()) {
            return Result.error("角色列表为空");
        } else {
            return Result.success(roles);
        }
    }
}