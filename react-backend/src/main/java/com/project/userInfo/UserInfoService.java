package com.project.userInfo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.exception.DuplicateDataException;
import com.project.login.LoginRequest;
import com.project.login.LoginResponse;
import com.project.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class UserInfoService implements UserDetailsService {

	private final UserInfoRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtUtil jwtUtil;
	
	private final AuthenticationManager authenticationManager;
	
	/**
	 * 회원가입 메소드
	 */
	@Transactional
	public void userRegist(UserInfoRequest userRequest) {
		
		// 아이디 중복검사
		if (userRepository.existsByUserId(userRequest.getUserId())) {
			throw new DuplicateDataException("이미 사용 중인 아이디입니다.");
		}
		
		// 이메일 중복검사
		if (userRepository.existsByUserEmail(userRequest.getUserEmail())) {
			throw new DuplicateDataException("이미 사용 중인 이메일입니다.");
		}
		
		// 비밀번호 암호화
		String encodePassword = passwordEncoder.encode(userRequest.getUserPw());
		
		// UserInfo 엔티티 생성
		UserInfo userInfo = UserInfo.builder()
						.userId(userRequest.getUserId())
						.userPw(encodePassword)
						.userNm(userRequest.getUserNm())
						.userEmail(userRequest.getUserEmail())
						.role("ROLE_USER")
						.loginType("SITE")
						.build();
						
		// 생성된 엔티티를 저장
		userRepository.save(userInfo);
	}
	
	
	
	/*
	 * [관리자]
	 * 전체 회원 조회 메소드
	 */
	public List<UserInfoResponse> getAllUserList() {
		return userRepository.findAll()
					.stream()
					.map(UserInfoResponse::from)
					.collect(Collectors.toList());
	}


	/**
     * Spring Security가 "신분증(UserDetails)"을 요청할 때 호출
     */
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return userRepository.findByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
	}
	
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
        
        // 3. 토큰을 DTO에 담아 반환
        return new LoginResponse(token);
	}
}
