package com.aircookies.smtworkordermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 产品实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id; // 产品id
    private String code;    // 产品编号
    private String name;    // 产品名称
    private String spec;    // 产品规格
    private String image;    // 产品图片
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
