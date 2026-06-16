package com.aircookies.smtworkordermanagement.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 产品查询 DTO
 * <p>
 * 封装产品列表查询的筛选条件和分页参数。
 * 所有字段均为可选，不传则不过滤该条件。
 * </p>
 */
@Data
public class QueryProductDTO {
    /** 产品名称（模糊查询） */
    private String name;
    /** 产品编号（模糊查询） */
    private String code;
    /** 创建时间 */
    private LocalDate createTime;
    /** 页码（从 1 开始） */
    private Integer pageNum;
    /** 每页大小 */
    private Integer pageSize;
}