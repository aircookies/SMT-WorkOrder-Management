package com.aircookies.smtworkordermanagement.entity;

// 产线实体类
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Line {
    private Long id;    // 产线ID
    private String name;    // 产线名称
    private String description;    // 产线描述
    private LocalDateTime createTime;    // 创建时间
    private LocalDateTime updateTime;    // 修改时间
}
