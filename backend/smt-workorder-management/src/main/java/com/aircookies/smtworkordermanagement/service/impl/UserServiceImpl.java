package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.CacheConstants;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import com.aircookies.smtworkordermanagement.service.UserService;
import com.aircookies.smtworkordermanagement.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.aircookies.smtworkordermanagement.common.Result.success;

@Service
public class UserServiceImpl implements UserService {
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    @Autowired
    public UserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, RedisUtil redisUtil) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.redisUtil = redisUtil;
    }

    // 添加用户
    @Override
    public Result addUser(SysUser user) {
        // 如果密码为空则使用默认密码(123456)
        if (user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("123456"));
        }

        // 加密用户密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 添加用户
        int res = sysUserMapper.addUser(user);
        if (res != 0) {
            return success();
        } else {
            throw new BusinessException("添加用户失败");
        }
    }

    // 根据ID删除用户
    @Override
    public Result deleteUser(List<Long> ids) {
        return Result.success(sysUserMapper.deleteUser(ids));
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


    // 分页查询所有用户
    @Override
    public Result findAll(Integer pageNum, Integer pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> sysUsers = sysUserMapper.findAll();
        // 获取结果
        PageInfo<SysUser> pageInfo = new PageInfo<>(sysUsers);
        // 封装结果并返回
        return success(
                new PagesDTO<>(
                        pageInfo.getPageNum(),
                        pageInfo.getPageSize(),
                        pageInfo.getTotal(),
                        pageInfo.getList()
                )
        );
    }

    // 修改用户
    @Override
    public Result updateUser(SysUser user) {
        user.setUpdateTime(null);
        sysUserMapper.updateUser(user);
        return Result.success();
    }
}
