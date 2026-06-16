package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求 DTO
 * <p>
 * 前端发送登录请求时使用，用户名和密码均经过 RSA 公钥加密。
 * 后端收到后先用 RSA 私钥解密，再进行认证验证。
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    /** 用户名（RSA 加密后的 Base64 字符串） */
    private String username;
    /** 密码（RSA 加密后的 Base64 字符串） */
    private String password;
}