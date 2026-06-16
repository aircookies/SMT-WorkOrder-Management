package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;

/**
 * 部门服务接口
 * <p>
 * 定义部门管理的核心业务操作，包括增删改查。
 * 查询结果使用 Redis 缓存，过期时间 24 小时。
 * </p>
 */
public interface DeptService {
    /** 添加部门 */
    Result addDept(Dept dept);

    /** 根据 ID 删除部门 */
    Result deleteDept(int id);

    /** 条件查询部门（不传条件则返回所有） */
    Result findDept(Dept dept);

    /** 修改部门信息 */
    Result updateDept(Dept dept);
}