package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Spring Security 自定义用户详情服务实现
 * <p>
 * 实现 UserDetailsService 接口，从数据库加载用户信息供 Spring Security 进行认证。
 * 将用户角色 ID 映射为 Spring Security 的 GrantedAuthority（格式：ROLE_{roleId}），
 * 并通过 @PreAuthorize("hasAnyRole('1')") 等方式在控制器层进行权限控制。
 * 同时根据用户的状态（status）控制账户的启用/禁用状态。
 * </p>
 */
@Slf4j
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final SysUserMapper sysUserMapper;

    @Autowired
    public CustomUserDetailsServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // 查询用户信息
        SysUser user = sysUserMapper.findUserByUserName(username);
        if (Objects.isNull(user)) {
            log.debug("用户名或密码错误，用户名：{}", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 验证用户权限
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + (user.getRoleId() != null ? user.getRoleId().toString() : "GUEST"))
        );

        // 封装UserDetails并返回
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == null || user.getStatus() == 1,
                true,
                true,
                true,
                authorities
        );
    }
}