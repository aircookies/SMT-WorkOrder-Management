package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.SysRole;

/**
 * 角色服务接口
 * <p>
 * 定义系统角色管理（RBAC 权限体系）的核心业务操作，包括增删改查。
 * 角色是权限控制的基础，不同角色拥有不同的功能访问权限。
 * 查询结果使用 Redis 缓存，过期时间 24 小时。
 * </p>
 */
public interface SysRoleService {
    /** 添加角色 */
    Result addRole(SysRole sysRole);

    /** 更新角色 */
    Result updateRole(SysRole sysRole);

    /** 根据 ID 删除角色 */
    Result deleteRole(Integer id);

    /** 根据 ID 查询角色 */
    Result findById(Integer id);

    /** 查询所有角色 */
    Result findAll();

    /** 条件查询角色 */
    Result findRoles(SysRole sysRole);
}