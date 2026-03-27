package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用于封装多个查询参数
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryUserDTO {
    private String name;    // 姓名
    private Integer gender; // 性别
    private Long roleId;    // 角色id
    private Long departmentId; // 部门id
    private Integer pageNum; // 页码
    private Integer pageSize; // 每页数量
}
