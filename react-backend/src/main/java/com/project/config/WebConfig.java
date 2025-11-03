package com.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 1. "/api/"로 시작하는 모든 요청에 대해
                .allowedOrigins("http://localhost:3000") // 2. React 개발 서버(localhost:3000)의 요청을 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메서드
                .allowedHeaders("*") // 4. 모든 헤더 허용
                .allowCredentials(true) // 5. 쿠키/인증 정보 전송 허용
                .maxAge(3600); // 6. Pre-flight 요청 캐시 시간 (1시간)
    }
}
