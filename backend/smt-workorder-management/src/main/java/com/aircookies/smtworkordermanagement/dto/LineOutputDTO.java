package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 产线产量统计DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineOutputDTO {
    private Integer lineId; // 产线ID
    private String lineName; // 产线名称
    private Integer totalPlanQuantity; // 总计划数量
    private Integer completedQuantity; // 完成数量
    private Integer producingQuantity; // 生产数量
    private Integer pendingQuantity; // 待生产数量
    private Integer closedQuantity; // 关闭数量
}
