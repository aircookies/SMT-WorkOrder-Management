package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;

public interface UserService {
    Result addUser(SysUser user);

    Result deleteUser(long id);

    Result UserList(QueryUserDTO keyword);

    Result updateUser(SysUser user);

    Result findUser(long id);

    Result findPage(Integer pageNum, Integer pageSize);
}
