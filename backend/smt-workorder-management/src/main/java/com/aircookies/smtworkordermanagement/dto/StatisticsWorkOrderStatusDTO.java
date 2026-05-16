package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//封装统计各个状态的工单的数量
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsWorkOrderStatusDTO {
    private Integer status; // 工单状态
    private String statusName; // 工单状态名称
    private Integer orderCount; // 工单数量

//    private Integer quantity; // 计划生产数量
//    private Integer qualifiedQuantity; // 合格数量
//    private Integer badQuantity; // 不良数量

//    private Integer pendingCount; // 待生产数量 (status=0)
//    private Integer producingCount; // 生产中数量 (status=1)
//    private Integer completedCount; // 已完成数量 (status=2)
//    private Integer closedCount; // 已关闭数量 (status=3)
}
