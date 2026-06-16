package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色数据访问层（MyBatis Mapper）
 * <p>
 * 对应 XML 映射文件，提供角色表的 CRUD 操作。
 * 角色用于 RBAC 权限体系。
 * </p>
 */
@Mapper
public interface SysRoleMapper {
    /** 添加角色 */
    int addRole(SysRole sysRole);

    /** 更新角色 */
    int updateRole(SysRole sysRole);

    /** 根据 ID 删除角色 */
    int deleteRole(Integer id);

    /** 根据 ID 查询角色 */
    SysRole findById(Integer id);

    /** 查询所有角色 */
    List<SysRole> findAll();

    /** 条件查询角色列表 */
    List<SysRole> findRoles(SysRole sysRole);
}