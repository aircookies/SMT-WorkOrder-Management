package com.aircookies.smtworkordermanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 产线实体类
 * <p>
 * 对应数据库中的产线表（line），表示 SMT 工厂中的生产产线。
 * 每条产线包含印刷、贴片、回流焊等不同工序，工单需要分配到具体产线上执行。
 * </p>
 */
@Data
public class Line {
    /** 产线 ID（主键） */
    private Long id;
    /** 产线名称 */
    private String name;
    /** 产线描述 */
    private String description;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 修改时间 */
    private LocalDateTime updateTime;
}