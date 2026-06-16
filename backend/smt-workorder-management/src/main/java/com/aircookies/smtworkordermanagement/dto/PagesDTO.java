package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应 DTO
 * <p>
 * 封装分页查询结果的通用数据结构，包含分页元数据和数据列表。
 * 使用泛型以适配不同类型的分页数据（用户列表、工单列表等）。
 * </p>
 *
 * @param <T> 数据列表的元素类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagesDTO<T> {
    /** 当前页码 */
    private Integer pageNum;
    /** 每页显示数量 */
    private Integer pageSize;
    /** 总记录数 */
    private Long total;
    /** 当前页的数据列表 */
    private List<T> list;
}