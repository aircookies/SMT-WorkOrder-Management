package com.aircookies.smtworkordermanagement.config;

import com.aircookies.smtworkordermanagement.service.impl.JwtTokenCacheService;
import com.aircookies.smtworkordermanagement.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * JWT认证过滤器
 * 继承自OncePerRequestFilter，确保每个请求只执行一次过滤逻辑
 * 负责从请求中提取JWT令牌并进行验证，验证通过后将用户认证信息设置到Security上下文
 */
@Component
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
     * 2. 验证令牌有效性（优先使用缓存校验，缓存未命中则使用JWT校验）
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

        // 从请求中提取JWT令牌
        String token = extractToken(request);

        // 如果没有令牌，直接放行（后续由Spring Security根据配置决定是否拦截）
        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 优先使用缓存校验令牌，缓存未命中则使用JWT工具类进行校验
        if (!jwtTokenCacheService.isTokenValid(token)) {
            logger.info("缓存未命中，使用JWT工具类校验令牌");
            // 缓存未命中，使用JWT工具类校验令牌
            if (!jwtUtil.validateToken(token)) {
                logger.info("JWT令牌校验失败");
                // 令牌无效，返回401未授权状态码
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // 从令牌中解析出用户名
        String username = jwtUtil.getUsernameFromToken(token);

        // 根据用户名加载用户详情（包含密码、权限等信息）
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!userDetails.isEnabled()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Strict")
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        // 设置认证的额外详情（如IP、Session等）
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        // 将认证信息存入Security上下文，标记当前请求已认证
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 如果缓存中不存在该令牌，则将其缓存以便后续快速校验
        if (!jwtTokenCacheService.isTokenValid(token)) {
            jwtTokenCacheService.cacheToken(token, userDetails);
        }

        // 校验通过，继续执行过滤器链
        filterChain.doFilter(request, response);
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