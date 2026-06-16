package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询 DTO
 * <p>
 * 封装用户列表查询的多个条件，支持多维度组合查询和分页。
 * 所有字段均为可选，不传则不过滤该条件。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryUserDTO {
    /** 姓名（模糊匹配） */
    private String name;
    /** 性别：1=男，0=女 */
    private Integer gender;
    /** 角色 ID */
    private Long roleId;
    /** 部门 ID */
    private Long departmentId;
    /** 页码（从 1 开始） */
    private Integer pageNum;
    /** 每页数量 */
    private Integer pageSize;
}