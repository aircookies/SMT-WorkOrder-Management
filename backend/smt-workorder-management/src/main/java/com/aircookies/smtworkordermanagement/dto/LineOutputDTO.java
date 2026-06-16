package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产线产量统计 DTO
 * <p>
 * 用于按产线维度统计在指定日期范围内的产量分布，
 * 包括总计划量、已完成、生产中、待生产、已关闭等各状态的数量。
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineOutputDTO {
    /** 产线 ID */
    private Integer lineId;
    /** 产线名称 */
    private String lineName;
    /** 总计划数量 */
    private Integer totalPlanQuantity;
    /** 完成数量 */
    private Integer completedQuantity;
    /** 生产数量 */
    private Integer producingQuantity;
    /** 待生产数量 */
    private Integer pendingQuantity;
    /** 关闭数量 */
    private Integer closedQuantity;
}