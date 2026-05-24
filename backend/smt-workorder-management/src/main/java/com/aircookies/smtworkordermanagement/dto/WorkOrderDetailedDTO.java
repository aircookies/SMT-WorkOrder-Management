package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 工单详细信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderDetailedDTO {
    private LocalDate date; // 工单创建日期
    private Long id; // 工单ID
    private Long productId; // 产品ID
    private Long LineId; // 线体ID
    private Integer quantity; // 计划生产数量
    private Integer status; // 工单状态
    private String statusName; // 工单状态名称
    private Integer priority; // 工单优先级
    private LocalDate planningTime; // 计划完成时间
    private Integer creatorId; // 创建人ID
    private String remarks; // 工单备注
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
