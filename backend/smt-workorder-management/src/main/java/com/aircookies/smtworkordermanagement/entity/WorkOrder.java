package com.aircookies.smtworkordermanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

// 工单实体类(核心)
@Data
public class WorkOrder {
    private Long id; // 工单ID(主键)
    private Long productId; // 产品ID(外键)
    private Long lineId; // 产线ID(外键)
    private Integer quantity; // 计划生产数量
    private Integer status; // 工单状态(0:待生产, 1:生产中, 2:生产完成, 3:已关闭)
    private Integer priority; // 优先级(0:普通, 1:中, 2:高, 3:紧急)
    private LocalDateTime finishTime; // 完成时间
    private Integer creatorId; // 创建人ID(外键)
    private String remarks; // 备注
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    
    // 关联查询字段
    private String productName; // 产品名称
    private String lineName; // 产线名称
    private String creatorName; // 创建人名称
}
