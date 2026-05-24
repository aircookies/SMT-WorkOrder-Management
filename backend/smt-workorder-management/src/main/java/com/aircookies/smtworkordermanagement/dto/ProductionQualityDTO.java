package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionQualityDTO {
    private LocalDate date; // 日期
    private Integer qualifiedQuantity; // 良品数
    private Integer badQuantity; // 不良数
    private Integer totalQuantity; // 总数
    private Double passRate; // 通过率，保留两位小数
}
