package com.aircookies.smtworkordermanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 产品实体类
 * <p>
 * 对应数据库中的产品表（product），记录需要生产的 SMT 产品信息。
 * 每个工单关联一个产品，产品包含编号、名称、规格和图片等信息。
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    /** 产品 ID（主键） */
    private Long id;
    /** 产品编号 */
    private String code;
    /** 产品名称 */
    private String name;
    /** 产品规格 */
    private String spec;
    /** 产品图片路径 */
    private String image;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}