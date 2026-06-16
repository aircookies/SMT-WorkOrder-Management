package com.aircookies.smtworkordermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门实体类
 * <p>
 * 对应数据库中的部门表（dept），用于管理工厂的组织架构。
 * 部门是用户管理的维度之一，用户可以归属到不同部门。
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dept {
    /** 部门 ID（主键） */
    private Integer id;
    /** 部门名称 */
    private String name;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 修改时间 */
    private LocalDateTime updateTime;
}