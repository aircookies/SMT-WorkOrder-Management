package com.aircookies.smtworkordermanagement.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import static com.aircookies.smtworkordermanagement.common.Result.success;
import com.aircookies.smtworkordermanagement.dto.PagesDTO;
import com.aircookies.smtworkordermanagement.dto.QueryUserDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import com.aircookies.smtworkordermanagement.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务实现类
 * <p>
 * 实现用户管理的核心业务逻辑，包括：
 * </p>
 * <ul>
 *   <li>添加用户（自动加密密码，冲突检测用户名）</li>
 *   <li>批量删除用户</li>
 *   <li>条件查询/分页查询用户</li>
 *   <li>修改用户信息（自动更新密码加密和修改时间）</li>
 * </ul>
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 添加用户
    @Override
    public Result addUser(SysUser user) {
        // 检查用户名是否已存在
        if (sysUserMapper.findUserByUserName(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 如果密码为空则使用默认密码(123456)
        if (user.getPassword().isEmpty()) {
            // 加密用户密码
            user.setPassword(passwordEncoder.encode("123456"));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // 添加用户
        int res = sysUserMapper.addUser(user);
        if (res != 0) {
            return success();
        } else {
            log.warn("添加用户失败，用户名：{}", user.getUsername());
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
        return success(sysUserMapper.findById(id));
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
        // 检查用户是否存在
        if (user == null || sysUserMapper.findById(user.getId()) == null) {
            throw new BusinessException("用户不存在");
        }

        // 加密用户密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 更新修改时间
        user.setUpdateTime(LocalDateTime.now());
        log.info("修改用户, 新的用户信息：{}", user);
        int res = sysUserMapper.updateUser(user);
        if (res != 0) {
            log.info("修改用户成功，用户名：{}", user.getUsername());
            return Result.success();
        } else {
            log.warn("修改用户失败，用户名：{}", user.getUsername());
            throw new BusinessException("修改用户失败");
        }
    }
}