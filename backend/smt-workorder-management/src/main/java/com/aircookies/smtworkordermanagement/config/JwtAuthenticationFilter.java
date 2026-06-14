package com.aircookies.smtworkordermanagement.config;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.service.impl.JwtTokenCacheService;
import com.aircookies.smtworkordermanagement.util.JWTUtil;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JWT认证过滤器
 * 继承自OncePerRequestFilter，确保每个请求只执行一次过滤逻辑
 * 负责从请求中提取JWT令牌并进行验证，验证通过后将用户认证信息设置到Security上下文
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Spring Security的用户详情服务，用于加载用户信息
    private final UserDetailsService userDetailsService;
    // JWT工具类，提供令牌的生成、解析和验证功能
    private final JWTUtil jwtUtil;
    // JWT令牌缓存服务，用于缓存和校验已颁发的令牌
    private final JwtTokenCacheService jwtTokenCacheService;

    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JWTUtil jwtUtil, JwtTokenCacheService jwtTokenCacheService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.jwtTokenCacheService = jwtTokenCacheService;
    }

    /**
     * 核心过滤逻辑
     * 1. 从请求中提取JWT令牌
     * 2. 验证令牌有效性（优先使用缓存校验，缓存未命中则解析JWT校验）
     * 3. 校验通过后加载用户信息并设置到Security上下文中
     *
     * @param request     HTTP请求对象
     * @param response    HTTP响应对象
     * @param filterChain 过滤器链，用于将请求传递给下一个过滤器
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean cached = jwtTokenCacheService.isTokenValid(token);
        Claims claims;

        if (cached) {
            try {
                claims = jwtUtil.parseToken(token);
            } catch (Exception e) {
                log.info("缓存命中但JWT令牌解析失败，可能密钥已变更");
                jwtTokenCacheService.invalidateToken(token);
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "登录已过期，请重新登录");
                return;
            }
        } else {
            try {
                claims = jwtUtil.parseToken(token);
            } catch (Exception e) {
                log.info("JWT令牌解析失败");
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "登录已过期，请重新登录");
                return;
            }
            if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
                log.info("JWT令牌已过期");
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "登录已过期，请重新登录");
                return;
            }
        }

        String username = claims.getSubject();

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.info("用户不存在: {}", username);
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户不存在");
            return;
        }

        if (!userDetails.isEnabled()) {
            jwtTokenCacheService.invalidateToken(token);
            clearTokenCookie(response);
            writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "账户已被禁用");
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (!cached) {
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("username", username);
            jwtTokenCacheService.cacheToken(token, tokenInfo);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 清除JWT_TOKEN Cookie
     */
    private void clearTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 写入JSON格式的错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(status);
        response.getWriter().write(JSON.toJSONString(Result.error(status, message)));
    }

    /**
     * 从HTTP请求中提取JWT令牌
     * 支持两种提取方式：
     * 1. 从Authorization请求头中提取Bearer令牌
     * 2. 从Cookie中提取名为JWT_TOKEN的令牌
     *
     * @param request HTTP请求对象
     * @return 提取到的JWT令牌字符串，如果未找到则返回null
     */
    private String extractToken(HttpServletRequest request) {
        // 方式一：从Authorization请求头中提取Bearer令牌
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 方式二：从Cookie中提取JWT_TOKEN
        if (request.getCookies() != null) {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                    .filter(c -> "JWT_TOKEN".equals(c.getName()))
                    .findFirst();

            if (cookie.isPresent()) {
                return cookie.get().getValue();
            }
        }

        // 未找到令牌
        return null;
    }
}