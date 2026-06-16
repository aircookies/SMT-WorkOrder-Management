package com.aircookies.smtworkordermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统角色实体类
 * <p>
 * 对应数据库中的角色表（sys_role），用于实现基于角色的访问控制（RBAC）。
 * 不同角色拥有不同的功能访问权限，如管理员（角色ID=1）拥有所有权限。
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRole {
    /** 角色 ID（主键） */
    private Integer id;
    /** 角色名称（如：管理员、生产计划员、车间操作员） */
    private String name;
    /** 角色描述 */
    private String description;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 修改时间 */
    private LocalDateTime updateTime;
}