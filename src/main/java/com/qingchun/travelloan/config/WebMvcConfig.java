package com.qingchun.travelloan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 确保 CORS 在 Spring MVC 层面也生效
 * 
 * @author Qingchun Team
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://117.72.99.34",
                    "http://117.72.99.34:80",
                    "http://localhost",
                    "http://localhost:3000",
                    "http://localhost:80",
                    "http://127.0.0.1",
                    "http://127.0.0.1:3000",
                    "http://127.0.0.1:80"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders("Authorization", "Content-Type")
                .maxAge(3600);
    }
}

