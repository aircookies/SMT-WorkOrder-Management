package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserMapper {
    int addUser(SysUser user);

    int deleteUser(List<Long> ids);

    List<SysUser> UserList(QueryUserDTO keyword);

    int updateUser(SysUser user);

    SysUser findById(long id);

    List<SysUser> findAll();

    SysUser findUserByUserName(String username);
}
