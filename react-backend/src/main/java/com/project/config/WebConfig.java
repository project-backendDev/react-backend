package com.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Spring Security의 .cors() 설정에 사용될
 * CorsConfigurationSource Bean을 등록합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer { // WebMvcConfigurer는 필수는 아니지만, 명시적으로 추가

    /**
     * 이 @Bean이 SecurityConfig의 
     * .corsConfigurationSource(webConfig.corsConfigurationSource())에 의해 사용됩니다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // React 개발 서버(localhost:3000)의 요청을 허용
        config.setAllowedOrigins(List.of("http://localhost:3000")); 
        
        // 허용할 HTTP 메서드 (GET, POST, PUT, DELETE, OPTIONS 등)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 허용할 HTTP 헤더 (모두 허용)
        config.setAllowedHeaders(List.of("*"));
        
        // 인증 정보(쿠키, JWT 토큰 헤더)를 허용
        config.setAllowCredentials(true);

        // 모든 경로(/api/** 등)에 대해 위 설정을 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); 
        
        return source;
    }
}