package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 各产品生产数量统计DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionQuantityDTO {
    private Integer productId; // 产品ID
    private String productCode; // 产品编码
    private String productName; // 产品名称
    private String productSpec; // 产品规格
    private Integer totalPlanQuantity; // 总计划生产数量
    private Integer completedQuantity; // 已完成数量
    private Integer producingQuantity; // 正在生产数量
    private Integer pendingQuantity; // 待生产数量
    private Integer closedQuantity; // 已关闭数量
}
