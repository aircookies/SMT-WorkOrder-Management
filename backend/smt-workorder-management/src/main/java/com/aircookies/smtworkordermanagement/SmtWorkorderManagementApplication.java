package com.aircookies.smtworkordermanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SMT工单管理系统 - Spring Boot 启动类
 * <p>
 * 该应用用于管理SMT（表面贴装技术）生产线的工单、产品、产线、部门及用户等业务。
 * 主要功能包括工单的创建与跟踪、工序报工、产量统计、用户权限管理（基于RBAC）等。
 * </p>
 *
 * <ul>
 *   <li>@SpringBootApplication - 启动类注解，启用自动配置</li>
 *   <li>@MapperScan - 扫描 MyBatis Mapper 接口</li>
 *   <li>@EnableScheduling - 启用定时任务调度</li>
 * </ul>
 *
 * @author aircookies
 */
@SpringBootApplication
@MapperScan("com.aircookies.smtworkordermanagement.mapper")
@EnableScheduling
public class SmtWorkorderManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmtWorkorderManagementApplication.class, args);
    }

}