package com.project.userInfo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.exception.DuplicateDataException;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class UserInfoService {

	private final UserInfoRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	/**
	 * 회원가입 메소드
	 */
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
	 * 전체 회원 조회 메소드
	 */
	public List<UserInfoResponse> getAllUserList() {
		return userRepository.findAll()
					.stream()
					.map(UserInfoResponse::from)
					.collect(Collectors.toList());
	}
}
