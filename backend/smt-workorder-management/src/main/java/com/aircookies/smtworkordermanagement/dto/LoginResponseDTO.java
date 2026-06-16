package com.aircookies.smtworkordermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 * <p>
 * 登录成功后返回的用户信息和 JWT 令牌。
 * 出于安全考虑，token 字段在返回给前端前会被设置为 null（令牌通过 HttpOnly Cookie 传递）。
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    /** JWT 令牌（仅内部使用，不返回给前端） */
    private String token;
    /** 用户 ID */
    private Long userId;
    /** 用户名（登录账号） */
    private String username;
    /** 真实姓名 */
    private String name;
    /** 角色 ID */
    private String roleId;
    /** 角色名称 */
    private String roleName;
}