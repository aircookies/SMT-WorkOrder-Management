package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 封装所有计划生产数量，合格数量，不良数量
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsGoodsDTO {
    private Integer status; // 工单状态
    private String statusName; // 工单状态名称

    private Integer quantity; // 计划生产数量
    private Integer qualifiedQuantity; // 合格数量
    private Integer badQuantity; // 不良数量
}
