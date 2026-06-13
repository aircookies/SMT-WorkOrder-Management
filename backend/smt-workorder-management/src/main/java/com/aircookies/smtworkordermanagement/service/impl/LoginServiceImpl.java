package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;
import com.aircookies.smtworkordermanagement.dto.LoginResponseDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import com.aircookies.smtworkordermanagement.service.LoginService;

import com.aircookies.smtworkordermanagement.util.JWTUtil;
import com.aircookies.smtworkordermanagement.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    private final SysUserMapper sysUserMapper;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RSAUtil rsaUtil;
    private final JwtTokenCacheService jwtTokenCacheService;

    @Autowired
    public LoginServiceImpl(SysUserMapper sysUserMapper,
                            JWTUtil jwtUtil,
                            PasswordEncoder passwordEncoder,
                            RSAUtil rsaUtil,
                            JwtTokenCacheService jwtTokenCacheService
                            ) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.rsaUtil = rsaUtil;
        this.jwtTokenCacheService = jwtTokenCacheService;
    }

    @Override
    public Result login(LoginDTO loginDTO) {
        if (Objects.isNull(loginDTO)) {
            log.error("登录参数不能为空");
            throw new NullPointerException("登录参数不能为空");
        }

        if (loginDTO.getUsername().isEmpty() || loginDTO.getPassword().isEmpty()) {
            log.error("用户名或密码不能为空");
            throw new NullPointerException("用户名或密码不能为空");
        }

        SysUser user = sysUserMapper.findUserByUserName(loginDTO.getUsername());
        if (user == null) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账户已被禁用");
        }

        // 解密密码
        String decryptedPassword;
        try {
            decryptedPassword = rsaUtil.decrypt(loginDTO.getPassword());
        } catch (Exception e) {
            log.error("解密密码失败", e);
            throw new IllegalArgumentException("解密失败");
        }
        if (!passwordEncoder.matches(decryptedPassword, user.getPassword())) {
                throw new BadCredentialsException("用户名或密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roleId", user.getRoleId());
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("userId", user.getId());
        tokenInfo.put("username", user.getUsername());
        tokenInfo.put("name", user.getName());
        tokenInfo.put("roleId", user.getRoleId());
        tokenInfo.put("roleName", user.getRoleName());
        tokenInfo.put("loginTime", System.currentTimeMillis());
        jwtTokenCacheService.cacheToken(token, tokenInfo);


        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                token,
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRoleId().toString(),
                user.getRoleName()
        );

        return Result.success(loginResponseDTO);
    }

}