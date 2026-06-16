package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;

import java.util.List;

/**
 * 用户服务接口
 * <p>
 * 定义用户管理的核心业务操作，包括增删改查和分页查询。
 * 密码在持久化前会使用 BCrypt 加密存储。
 * </p>
 */
public interface UserService {
    /** 添加用户（密码为空时自动使用默认密码并加密） */
    Result addUser(SysUser user);

    /** 批量删除用户 */
    Result deleteUser(List<Long> ids);

    /** 条件查询用户（分页） */
    Result UserList(QueryUserDTO keyword);

    /** 修改用户信息（密码变化时重新加密） */
    Result updateUser(SysUser user);

    /** 根据 ID 查询用户 */
    Result findUser(long id);

    /** 分页查询所有用户 */
    Result findAll(Integer pageNum, Integer pageSize);
}