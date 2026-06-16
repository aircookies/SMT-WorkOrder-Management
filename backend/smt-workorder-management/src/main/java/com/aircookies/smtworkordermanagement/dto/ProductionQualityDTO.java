package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 生产质量统计 DTO
 * <p>
 * 用于统计指定日期内的生产质量数据，包括良品数、不良数及通过率。
 * 通过率 = 良品数 / 总数，保留两位小数。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionQualityDTO {
    /** 统计日期 */
    private LocalDate date;
    /** 良品数量 */
    private Integer qualifiedQuantity;
    /** 不良数量 */
    private Integer badQuantity;
    /** 总数量（良品 + 不良） */
    private Integer totalQuantity;
    /** 通过率（良品数/总数），保留两位小数 */
    private Double passRate;
}