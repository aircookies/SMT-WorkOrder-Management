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

@RestController
@RequestMapping("/logout")
public class LogoutController {
    private final JwtTokenCacheService jwtTokenCacheService;

    @Autowired
    public LogoutController(JwtTokenCacheService jwtTokenCacheService) {
        this.jwtTokenCacheService = jwtTokenCacheService;
    }

    @PostMapping
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter(c -> "JWT_TOKEN".equals(c.getName()))
                .findFirst()
                .orElse(null);

        if (cookie != null) {
            jwtTokenCacheService.invalidateToken(cookie.getValue());
            clearCookie(request, response);
        }
        return Result.success("退出登录成功");
    }

    private void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        boolean isSecure = "https".equalsIgnoreCase(request.getScheme());
        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}