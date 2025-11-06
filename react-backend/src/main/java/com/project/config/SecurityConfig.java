package com.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.util.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final WebConfig webConfig;
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	/**
	 * 비밀번호 암호화 -> UserService에서 사용
	 * @return
	 */
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	/**
     * 인증 관리자(AuthenticationManager) Bean 등록
     * (UserService에서 "로그인 인증" 시 사용)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

	
	/**
     * Spring Security의 메인 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. API 서버이므로 CSRF, FormLogin, HttpBasic 비활성화
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 2. 세션을 사용하지 않고, JWT를 사용 (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. CORS 설정 (WebConfig에서 만든 Bean을 사용)
            .cors(cors -> cors.configurationSource(webConfig.corsConfigurationSource()))

            // 4. API 경로별 권한 설정
            .authorizeHttpRequests(authorize -> authorize
                // 회원가입, 로그인은 누구나 허용 (permitAll)
                .requestMatchers("/api/user/signup", "/api/user/login", "/api/user/me").permitAll()
                // 관리자 API는 "ROLE_ADMIN" 권한 필요 (ROLE_ 제외하고 "ADMIN"만 씀)
                .requestMatchers("/api/mngr/**").hasRole("ADMIN") 
                // 그 외 /api/로 시작하는 모든 요청은 인증(로그인) 필요
                .requestMatchers("/api/**").authenticated() 
                // (React 라우터가 페이지를 로드할 수 있도록 그 외 경로는 허용)
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
