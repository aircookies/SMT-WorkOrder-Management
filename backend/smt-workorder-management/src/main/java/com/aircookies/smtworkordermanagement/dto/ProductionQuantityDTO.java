package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品产量统计 DTO
 * <p>
 * 用于按产品维度统计在指定日期范围内的生产数量分布，
 * 包括总计划量、已完成、生产中、待生产、已关闭等各状态的数量。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionQuantityDTO {
    /** 产品 ID */
    private Integer productId;
    /** 产品编码 */
    private String productCode;
    /** 产品名称 */
    private String productName;
    /** 产品规格 */
    private String productSpec;
    /** 总计划生产数量 */
    private Integer totalPlanQuantity;
    /** 已完成数量 */
    private Integer completedQuantity;
    /** 正在生产数量 */
    private Integer producingQuantity;
    /** 待生产数量 */
    private Integer pendingQuantity;
    /** 已关闭数量 */
    private Integer closedQuantity;
}