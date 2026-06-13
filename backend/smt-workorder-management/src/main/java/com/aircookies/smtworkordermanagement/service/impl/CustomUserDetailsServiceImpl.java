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