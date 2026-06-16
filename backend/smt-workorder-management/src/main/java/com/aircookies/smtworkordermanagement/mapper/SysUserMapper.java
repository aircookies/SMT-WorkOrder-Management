package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户数据访问层（MyBatis Mapper）
 * <p>
 * 对应 XML 映射文件，提供用户表的 CRUD 操作和条件查询。
 * 方法签名与对应的 UserService 对齐。
 * </p>
 */
@Mapper
public interface SysUserMapper {
    /** 添加用户 */
    int addUser(SysUser user);

    /** 批量删除用户 */
    int deleteUser(List<Long> ids);

    /** 条件查询用户列表 */
    List<SysUser> UserList(QueryUserDTO keyword);

    /** 更新用户 */
    int updateUser(SysUser user);

    /** 根据 ID 查询用户 */
    SysUser findById(long id);

    /** 查询所有用户 */
    List<SysUser> findAll();

    /** 根据用户名查询用户（用于登录认证） */
    SysUser findUserByUserName(String username);
}