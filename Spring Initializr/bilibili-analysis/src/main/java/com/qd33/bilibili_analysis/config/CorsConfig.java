package com.qd33.bilibili_analysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许的前端地址
        config.addAllowedOrigin("http://localhost:5173"); // Vue开发服务器
        config.addAllowedOrigin("http://127.0.0.1:5173");
        config.addAllowedOrigin("http://localhost:5174"); // Vite备用端口
        config.addAllowedOrigin("http://127.0.0.1:5174");
        config.addAllowedOrigin("http://localhost:3000"); // 其他可能的端口

        // 开发环境允许所有来源
        config.addAllowedOriginPattern("*");

        // 允许的请求头
        config.addAllowedHeader("*");

        // 允许的请求方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");

        // 允许携带认证信息（如cookies）
        config.setAllowCredentials(true);

        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}