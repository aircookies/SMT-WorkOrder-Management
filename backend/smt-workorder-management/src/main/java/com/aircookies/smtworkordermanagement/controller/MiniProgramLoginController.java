package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.BusinessException;
import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;
import com.aircookies.smtworkordermanagement.dto.LoginResponseDTO;
import com.aircookies.smtworkordermanagement.entity.SysUser;
import com.aircookies.smtworkordermanagement.mapper.SysUserMapper;
import com.aircookies.smtworkordermanagement.service.impl.JwtTokenCacheService;
import com.aircookies.smtworkordermanagement.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 小程序专用登录控制器
 * <p>
 * 与 Web 端 LoginController 的区别：
 * 1. 接受明文密码（不进行 RSA 解密），小程序运行在 HTTPS 环境下，传输安全由 TLS 保障
 * 2. JWT Token 直接在响应体中返回（小程序不支持浏览器 Cookie 机制）
 * 3. 小程序端后续请求通过 Authorization: Bearer {token} 请求头携带令牌
 * </p>
 */
@RestController
@RequestMapping("/miniprogram")
@Slf4j
public class MiniProgramLoginController {

    private final SysUserMapper sysUserMapper;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenCacheService jwtTokenCacheService;

    @Autowired
    public MiniProgramLoginController(SysUserMapper sysUserMapper,
                                       JWTUtil jwtUtil,
                                       PasswordEncoder passwordEncoder,
                                       JwtTokenCacheService jwtTokenCacheService) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenCacheService = jwtTokenCacheService;
    }

    /**
     * 小程序登录接口
     * <p>
     * 接收明文用户名和密码，验证通过后在响应体中直接返回 JWT Token。
     * 小程序端需自行管理 Token 存储（本地缓存），并在后续请求中通过
     * Authorization: Bearer {token} 请求头携带令牌。
     * </p>
     *
     * @param loginDTO 登录请求体（明文用户名和密码）
     * @return 登录结果，包含 Token 和用户基本信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        if (Objects.isNull(loginDTO)) {
            throw new NullPointerException("登录参数不能为空");
        }

        if (loginDTO.getUsername() == null || loginDTO.getUsername().isEmpty()
                || loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
            throw new NullPointerException("用户名或密码不能为空");
        }

        // 查询用户
        SysUser user = sysUserMapper.findUserByUserName(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账户已被禁用");
        }

        // 直接使用明文密码进行 BCrypt 校验（无需 RSA 解密）
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成 JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roleId", user.getRoleId());
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        // 缓存 Token 到 Redis
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("userId", user.getId());
        tokenInfo.put("username", user.getUsername());
        tokenInfo.put("name", user.getName());
        tokenInfo.put("roleId", user.getRoleId());
        tokenInfo.put("roleName", user.getRoleName());
        tokenInfo.put("loginTime", System.currentTimeMillis());
        jwtTokenCacheService.cacheToken(token, tokenInfo);

        // 响应体中直接返回 Token（与 Web 端的关键区别）
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
