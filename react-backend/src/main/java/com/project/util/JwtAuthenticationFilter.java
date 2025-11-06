package com.project.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.userInfo.service.UserInfoCustomDetailService;
import com.project.userInfo.service.UserInfoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	
    private final UserInfoCustomDetailService userInfoCustomDetailService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, 
									HttpServletResponse response, 
									FilterChain filterChain) throws ServletException, IOException {
		
		// 1. 요청 헤더에서 토큰 추출
        String token = extractToken(request);

        // 2. 토큰이 존재하고 유효한지 검사
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            
            // 3. 토큰에서 사용자 ID(username) 추출
            String userId = jwtUtil.extractUsername(token);

            // 4. DB에서 사용자 정보(UserDetails) 조회
            UserDetails userDetails = userInfoCustomDetailService.loadUserByUsername(userId);

            // 5. "인증(Authentication)" 객체 생성 (비밀번호는 null)
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );

            // 6. (★핵심★) SecurityContextHolder에 인증 객체를 저장
            //    -> 이 요청은 이제 "인증된" 요청으로 처리됨
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
	}
    
	/**
     * Request Header에서 "Authorization" 토큰을 추출
     * (형식: "Bearer [토큰값]")
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " (7글자) 이후의 토큰 값만 반환
        }
        return null;
    }
}
