package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.dto.LoginDTO;

/**
 * 登录服务接口
 * <p>
 * 处理用户登录认证逻辑，包括 RSA 解密、密码验证和 JWT 令牌生成。
 * </p>
 */
public interface LoginService {
    /**
     * 用户登录
     * <p>
     * 使用 RSA 私钥解密用户名和密码，通过 Spring Security 认证管理器验证。
     * 验证通过后生成 JWT 令牌，并将令牌缓存到 Redis。
     * </p>
     *
     * @param loginDTO 加密的登录凭证
     * @return 登录结果（包含用户基本信息和 JWT 令牌）
     */
    Result login(LoginDTO loginDTO);
}