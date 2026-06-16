package com.aircookies.smtworkordermanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享（CORS）配置类
 * <p>
 * 为解决前后端分离架构中的跨域请求问题，配置允许前端开发服务器访问后端 API。
 * 生产环境中应替换为实际的前端域名。
 * </p>
 *
 * <p>当前配置允许：</p>
 * <ul>
 *   <li>来源：http://localhost:5173（Vite 开发服务器默认端口）</li>
 *   <li>方法：GET、POST、PUT、DELETE、OPTIONS</li>
 *   <li>请求头：所有（*）</li>
 *   <li>凭证：允许携带 Cookie</li>
 *   <li>预检缓存：3600 秒（1小时）</li>
 * </ul>
 */
@Configuration
public class CorsConfig {

    /**
     * 配置 CORS 过滤器
     *
     * @return CorsFilter 实例，全局生效
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许的前端来源（开发环境 Vite 默认端口）
        config.addAllowedOrigin("http://localhost:5173");

        // 允许携带 Cookie 等凭证信息
        config.setAllowCredentials(true);

        // 允许的 HTTP 方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 预检请求缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}