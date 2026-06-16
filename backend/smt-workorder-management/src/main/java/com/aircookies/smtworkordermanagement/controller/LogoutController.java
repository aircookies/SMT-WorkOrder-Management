package com.aircookies.smtworkordermanagement.controller;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.service.impl.JwtTokenCacheService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 注销控制器
 * <p>
 * 处理用户退出登录请求，清除 JWT 令牌缓存和客户端 Cookie，
 * 使令牌立即失效，防止令牌被重复使用。
 * </p>
 */
@RestController
@RequestMapping("/logout")
public class LogoutController {
    private final JwtTokenCacheService jwtTokenCacheService;

    @Autowired
    public LogoutController(JwtTokenCacheService jwtTokenCacheService) {
        this.jwtTokenCacheService = jwtTokenCacheService;
    }

    /**
     * 用户注销接口
     * <p>
     * 从 Cookie 中提取 JWT 令牌，在 Redis 中标记为失效，
     * 同时清除客户端 Cookie。
     * </p>
     *
     * @param request  HTTP 请求对象，用于提取 Cookie
     * @param response HTTP 响应对象，用于清除 Cookie
     * @return 注销结果
     */
    @PostMapping
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        // 从请求 Cookie 中提取 JWT 令牌
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter(c -> "JWT_TOKEN".equals(c.getName()))
                .findFirst()
                .orElse(null);

        if (cookie != null) {
            // 在 Redis 中标记令牌失效
            jwtTokenCacheService.invalidateToken(cookie.getValue());
            // 清除客户端 Cookie
            clearCookie(request, response);
        }
        return Result.success("退出登录成功");
    }

    /**
     * 清除客户端 JWT_TOKEN Cookie
     * <p>
     * 设置 maxAge=0 使浏览器立即删除该 Cookie。
     * </p>
     */
    private void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        boolean isSecure = "https".equalsIgnoreCase(request.getScheme());
        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(0)              // 立即过期，浏览器会删除该 Cookie
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}