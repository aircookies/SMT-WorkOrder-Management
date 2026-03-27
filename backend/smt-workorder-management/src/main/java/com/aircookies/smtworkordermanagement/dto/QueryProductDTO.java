package com.aircookies.smtworkordermanagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 产品查询DTO
 */
@Data
public class QueryProductDTO {
    private String name; // 产品名称（模糊查询）
    private String code; // 产品编号（模糊查询）
    private LocalDateTime createTimeBegin; // 创建开始时间
    private LocalDateTime createTimeEndTime; // 创建结束时间
    private Integer pageNum; // 页码
    private Integer pageSize; // 每页大小
}
