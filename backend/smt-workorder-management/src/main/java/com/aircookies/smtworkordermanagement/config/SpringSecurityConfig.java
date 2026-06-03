package com.aircookies.smtworkordermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // 禁用认证和授权
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 允许所有请求无需认证
                )
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护（调试时常用）
                .httpBasic(AbstractHttpConfigurer::disable) // 禁用HTTP基本认证
                .formLogin(AbstractHttpConfigurer::disable); // 禁用表单登录

        return http.build();
    }
}

