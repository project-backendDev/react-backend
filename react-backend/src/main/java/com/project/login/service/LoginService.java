package com.project.login.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.login.vo.LoginRequest;
import com.project.login.vo.LoginResponse;
import com.project.userInfo.repository.UserInfoRepository;
import com.project.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class LoginService {
	
	private final JwtUtil jwtUtil;
	
	private final AuthenticationManager authenticationManager;

	
	/**
	 * 로그인 메소드
	 * @param loginRequest
	 * @return
	 * @throws AuthenticationException
	 */
	@Transactional
	public LoginResponse loginProccess(LoginRequest loginRequest) throws AuthenticationException {
		
		// 1. AuthenticationManager에게 "인증"을 요청
        //    (이 과정에서 loadUserByUsername이 호출되고, 비밀번호 비교가 자동으로 일어남)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                		loginRequest.getUserId(),
                		loginRequest.getUserPw()
                )
        );
        
        // 2. 인증에 성공했다면, JwtUtil을 이용해 "권한이 포함된" 토큰을 생성
        String token = jwtUtil.generateToken(authentication);
        System.out.println("Token		::	" + token);
        
        // 3. 토큰을 DTO에 담아 반환
        return new LoginResponse(token);
	}


}
