package com.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF(Cross-Site Request Forgery) 보호 비활성화
            // React 같은 SPA와 API 서버 연동 시에는 보통 비활성화
            .csrf(AbstractHttpConfigurer::disable)

            // 2. HTTP Basic 인증 비활성화 (ID/PW 팝업창 끄기)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 3. 로그인 Alert 비활성화 
            .formLogin(AbstractHttpConfigurer::disable)

            // 4. 요청 경로별 권한 설정
            .authorizeHttpRequests(authorize -> authorize
                // '/api/**'로 시작하는 모든 요청은 허용
                .requestMatchers("/api/**").permitAll() 
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated() 
            );

        return http.build();
    }
}
