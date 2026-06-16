package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工单详细信息 DTO
 * <p>
 * 用于统计查询时返回工单的详细信息，包含工单基本信息和关联的状态中文名称。
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderDetailedDTO {
    /** 工单创建日期 */
    private LocalDate date;
    /** 工单 ID */
    private Long id;
    /** 产品 ID */
    private Long productId;
    /** 产线 ID */
    private Long LineId;
    /** 计划生产数量 */
    private Integer quantity;
    /** 工单状态（0=待生产，1=生产中，2=生产完成，3=已关闭） */
    private Integer status;
    /** 工单状态名称（中文） */
    private String statusName;
    /** 工单优先级（0=普通，1=中，2=高，3=紧急） */
    private Integer priority;
    /** 计划完成时间 */
    private LocalDate planningTime;
    /** 创建人 ID */
    private Integer creatorId;
    /** 工单备注 */
    private String remarks;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}