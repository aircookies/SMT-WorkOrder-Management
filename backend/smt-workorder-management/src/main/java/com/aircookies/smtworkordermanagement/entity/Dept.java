package com.aircookies.smtworkordermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 部门实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dept {
    private Integer id; // 部门id
    private String name;    // 部门名称
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 修改时间
}
