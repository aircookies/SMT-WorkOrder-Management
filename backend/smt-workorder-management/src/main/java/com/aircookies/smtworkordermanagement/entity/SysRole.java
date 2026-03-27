package com.aircookies.smtworkordermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 系统角色实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysRole {
    private Integer id; // 角色id
    private String name; // 角色名称
    private String description; // 描述
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 修改时间
}
