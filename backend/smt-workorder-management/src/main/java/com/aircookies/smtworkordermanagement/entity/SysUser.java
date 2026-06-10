package com.aircookies.smtworkordermanagement.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用户实体类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
    private Long id; // 用户id
    private String username; // 用户名
    private String password; // 密码
    private String name; // 真实姓名
    private Integer gender; // 性别: 1男 0女
    private Long roleId; // 角色id
    private String roleName; // 角色名称
    private Long departmentId; // 部门id
    private Integer status; // 状态: 1正常 0禁用
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间
}
