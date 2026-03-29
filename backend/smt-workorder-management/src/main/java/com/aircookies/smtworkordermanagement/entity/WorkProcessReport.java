package com.aircookies.smtworkordermanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

// 工单工序报工实体类
@Data
public class WorkProcessReport {
    private Long id; // 报工ID
    private Long orderId; // 工单ID
    private Integer processSeq; // 当前工序：1印刷，2贴片，3回流焊
    private Integer status; // 生产状态：0未开始，1进行中，2已完成
    private Integer qualifiedQuantity; // 合格数量
    private Integer badQuantity; // 不良数量
    private Long operatorId; // 操作员ID
    private String remarks; // 备注
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime finishTime; // 完成时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
