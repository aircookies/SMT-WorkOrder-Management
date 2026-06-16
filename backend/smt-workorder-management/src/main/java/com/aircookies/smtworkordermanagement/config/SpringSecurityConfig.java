package com.aircookies.smtworkordermanagement.config;

import com.alibaba.fastjson.JSON;
import com.aircookies.smtworkordermanagement.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

/**
 * Spring Security 安全配置类
 * <p>
 * 配置基于 JWT 的无状态认证方案，替代传统的 Session 认证。
 * 主要安全策略：
 * </p>
 * <ul>
 *   <li>禁用 CSRF（前后端分离，无 Cookie-Session 风险）</li>
 *   <li>禁用 HTTP Basic 认证和表单登录</li>
 *   <li>使用无状态会话（STATELESS），不创建 Session</li>
 *   <li>/publickey、/login、/logout 允许匿名访问</li>
 *   <li>其他所有请求需要认证</li>
 *   <li>在 UsernamePasswordAuthenticationFilter 之前插入 JWT 过滤器</li>
 *   <li>自定义认证失败和权限不足的 JSON 响应</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SpringSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 配置安全过滤链
     * <p>
     * 定义 URL 访问权限、异常处理、会话管理策略以及 JWT 过滤器的插入位置。
     * </p>
     *
     * @param http HttpSecurity 配置对象
     * @return 构建好的 SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // 禁用认证和授权
        http
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护（调试时常用）
                .httpBasic(AbstractHttpConfigurer::disable) // 禁用HTTP基本认证
                .formLogin(AbstractHttpConfigurer::disable) // 禁用表单登录
                .logout(AbstractHttpConfigurer::disable) // 禁用Spring Security默认的logout

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/publickey").permitAll()  // 获取公钥接口，无需认证
                                .requestMatchers("/login").permitAll()      // 登录接口，无需认证
                                .requestMatchers("/logout").permitAll()     // 注销接口，无需认证
                                .anyRequest().authenticated()               // 其他请求需要认证

                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 无状态会话
                )

                .exceptionHandling(ex -> ex
                        // 权限不足（403）处理 - 返回 JSON 格式错误
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setStatus(403);
                            response.getWriter().write(JSON.toJSONString(Result.error(403, "权限不足，无法访问该资源")));
                        })
                        // 未认证（401）处理 - 返回 JSON 格式错误
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setStatus(401);
                            response.getWriter().write(JSON.toJSONString(Result.error(401, "未登录或登录已过期")));
                        })
                )

                // 在 UsernamePasswordAuthenticationFilter 之前插入 JWT 认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码编码器
     * <p>
     * 使用 BCrypt 算法对密码进行哈希处理，每次加密结果不同（自带盐值），
     * 是目前主流的密码存储方案。
     * </p>
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * <p>
     * 从 AuthenticationConfiguration 中获取，用于手动处理认证逻辑。
     * </p>
     *
     * @param authenticationConfiguration 认证配置
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) {
        return authenticationConfiguration.getAuthenticationManager();
    }
}