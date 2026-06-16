package com.aircookies.smtworkordermanagement.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统用户实体类
 * <p>
 * 对应数据库中的用户表（sys_user），用于管理所有系统登录用户。
 * 用户拥有角色（RBAC权限控制）、所属部门、状态（启用/禁用）等属性。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
    /** 用户 ID（主键） */
    private Long id;
    /** 登录用户名 */
    private String username;
    /** 登录密码（BCrypt 加密存储） */
    private String password;
    /** 真实姓名 */
    private String name;
    /** 性别：1=男，0=女 */
    private Integer gender;
    /** 角色 ID（关联 sys_role 表） */
    private Long roleId;
    /** 角色名称（查询时联表带出，不持久化到此表） */
    private String roleName;
    /** 部门 ID（关联 dept 表） */
    private Long departmentId;
    /** 状态：1=正常，0=禁用 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 修改时间 */
    private LocalDateTime updateTime;
}