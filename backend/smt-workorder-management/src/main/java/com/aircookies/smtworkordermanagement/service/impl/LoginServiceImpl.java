package com.aircookies.smtworkordermanagement.service.impl;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;
import com.aircookies.smtworkordermanagement.dto.LoginResponseDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import com.aircookies.smtworkordermanagement.service.LoginService;

import com.aircookies.smtworkordermanagement.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {
    private final SysUserMapper sysUserMapper;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginServiceImpl(SysUserMapper sysUserMapper, JWTUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Result login(LoginDTO loginDTO) {
        if (Objects.isNull(loginDTO)) {
            return Result.error(422, "参数错误");
        }

        if (loginDTO.getUsername().isEmpty() || loginDTO.getPassword().isEmpty()) {
            return Result.error(422, "用户名或密码不能为空");
        }

        SysUser user = sysUserMapper.findUserByUserName(loginDTO.getUsername());
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roleId", user.getRoleId());
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                token,
                user.getUsername(),
                user.getName(),
                user.getRoleId() != null ? user.getRoleId().toString() : "USER"
        );

        return Result.success(loginResponseDTO);
    }

}
