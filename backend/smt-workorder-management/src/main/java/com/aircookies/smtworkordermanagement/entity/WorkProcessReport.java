package com.aircookies.smtworkordermanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单工序报工实体类
 * <p>
 * 对应数据库中的工序报工表（work_process_report），记录工单在产线上各工序的生产情况。
 * SMT 产线包含三个主要工序：印刷、贴片、回流焊，每个工序需要分别报工。
 * </p>
 *
 * <p>工序编号说明：</p>
 * <ul>
 *   <li>1 - 印刷：将锡膏印刷到 PCB 板上</li>
 *   <li>2 - 贴片：将元器件贴装到 PCB 板上</li>
 *   <li>3 - 回流焊：通过高温将元器件焊接固定</li>
 * </ul>
 */
@Data
public class WorkProcessReport {
    /** 报工 ID（主键） */
    private Long id;
    /** 工单 ID（外键） */
    private Long orderId;
    /** 当前工序：1=印刷，2=贴片，3=回流焊 */
    private Integer processSeq;
    /** 合格数量 */
    private Integer qualifiedQuantity;
    /** 不良数量 */
    private Integer badQuantity;
    /** 操作员 ID（外键，关联 sys_user 表） */
    private Long operatorId;
    /** 备注 */
    private String remarks;
    /** 工序开始时间 */
    private LocalDateTime startTime;
    /** 工序完成时间 */
    private LocalDateTime finishTime;
    /** 报工记录创建时间 */
    private LocalDateTime createTime;
    /** 报工记录更新时间 */
    private LocalDateTime updateTime;
}