package com.aircookies.smtworkordermanagement.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工单实体类（核心业务实体）
 * <p>
 * 对应数据库中的工单表（work_order），是 SMT 工单管理系统的核心实体。
 * 工单记录了生产什么产品、在哪条产线、计划生产数量、优先级、当前状态等关键信息。
 * </p>
 *
 * <p>工单状态流转：</p>
 * <ul>
 *   <li>0 - 待生产：工单已创建，等待开始生产</li>
 *   <li>1 - 生产中：工单已开始，正在进行各工序报工</li>
 *   <li>2 - 生产完成：所有工序已完成，产量达标</li>
 *   <li>3 - 已关闭：工单已归档关闭</li>
 * </ul>
 */
@Data
public class WorkOrder {
    /** 工单 ID（主键） */
    private Long id;
    /** 产品 ID（外键，关联 product 表） */
    private Long productId;
    /** 产线 ID（外键，关联 line 表） */
    private Long lineId;
    /** 计划生产数量 */
    private Integer quantity;
    /** 工单状态：0=待生产，1=生产中，2=生产完成，3=已关闭 */
    private Integer status;
    /** 优先级：0=普通，1=中，2=高，3=紧急 */
    private Integer priority;
    /** 计划完成时间 */
    private LocalDate planningTime;
    /** 创建人 ID（外键，关联 sys_user 表） */
    private Integer creatorId;
    /** 备注 */
    private String remarks;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;

    // ========== 以下为关联查询字段（非数据库持久化字段） ==========

    /** 产品名称（联表查询） */
    private String productName;
    /** 产线名称（联表查询） */
    private String lineName;
    /** 创建人名称（联表查询） */
    private String creatorName;
}