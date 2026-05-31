package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagesDTO<T> {
    private Integer pageNum;    // 当前页码
    private Integer pageSize;   // 每页显示数量
    private Long total;  // 总页数
    private List<T> list;  // 数据
}
