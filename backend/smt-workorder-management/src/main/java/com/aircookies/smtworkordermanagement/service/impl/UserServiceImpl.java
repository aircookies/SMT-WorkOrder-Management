package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import com.aircookies.smtworkordermanagement.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.aircookies.smtworkordermanagement.common.Result.success;

@Service
public class UserServiceImpl implements UserService {
    private final SysUserMapper sysUserMapper;

    @Autowired
    public UserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    // 添加用户
    @Override
    public Result addUser(SysUser user) {
        int res = sysUserMapper.addUser(user);
        if (res != 0) {
            return success();
        } else {
            return Result.fail(1, "添加用户失败");
        }
    }

    // 根据ID删除用户
    @Override
    public Result deleteUser(long id) {
        sysUserMapper.deleteUser(id);
        return Result.success();
    }

    // 查询所有用户或指定用户
    @Override
    public Result UserList(QueryUserDTO keyword) {
        // 开启分页
        PageHelper.startPage(keyword.getPageNum(), keyword.getPageSize());
        // 查询所有用户或搜索用户
        List<SysUser> sysUsers = sysUserMapper.UserList(keyword);
        // 获取分页结果
        PageInfo<SysUser> pageInfo = new PageInfo<>(sysUsers);
        return success(
                new PagesDTO<>(
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize(),
                        pageInfo.getTotal(),
                        pageInfo.getList()
                )
        );
    }

    // 根据ID查询用户
    @Override
    public Result findUser(long id) {
        return success(sysUserMapper.findUser(id));
    }


    // 查询所有用户
    @Override
    public Result findAll(Integer pageNum, Integer pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> sysUsers = sysUserMapper.findAll();
        // 获取结果
        PageInfo<SysUser> pageInfo = new PageInfo<>(sysUsers);
        // 封装结果并返回
        PagesDTO<SysUser> pagesDTO = new PagesDTO<>(
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getTotal(),
                pageInfo.getList()
        );
        return success(pagesDTO);
    }

    // 修改用户
    @Override
    public Result updateUser(SysUser user) {
        user.setUpdateTime(null);
        sysUserMapper.updateUser(user);
        return Result.success();
    }
}
