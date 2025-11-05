package com.project.userInfo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.exception.DuplicateDataException;
import com.project.userInfo.model.UserInfo;
import com.project.userInfo.repository.UserInfoRepository;
import com.project.userInfo.vo.UserInfoInsertRequest;
import com.project.userInfo.vo.UserInfoResponse;
import com.project.userInfo.vo.UserInfoUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
//@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {

	private final UserInfoRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	/**
	 * 회원가입 메소드
	 */
	@Transactional
	public void userRegist(UserInfoInsertRequest userInfoInsertRequest) {
		
		// 아이디 중복검사
		if (userRepository.existsByUserId(userInfoInsertRequest.getUserId())) {
			throw new DuplicateDataException("이미 사용 중인 아이디입니다.");
		}
		
		// 이메일 중복검사
		if (userRepository.existsByUserEmail(userInfoInsertRequest.getUserEmail())) {
			throw new DuplicateDataException("이미 사용 중인 이메일입니다.");
		}
		
		// 비밀번호 암호화
		String encodePassword = passwordEncoder.encode(userInfoInsertRequest.getUserPw());
		
		// UserInfo 엔티티 생성
		UserInfo userInfo = UserInfo.builder()
						.userId(userInfoInsertRequest.getUserId())
						.userPw(encodePassword)
						.userNm(userInfoInsertRequest.getUserNm())
						.userEmail(userInfoInsertRequest.getUserEmail())
						.role("ROLE_USER")
						.loginType("SITE")
						.build();
						
		// 생성된 엔티티를 저장
		userRepository.save(userInfo);
	}

	/**
     * Spring Security가 "신분증(UserDetails)"을 요청할 때 호출
     */
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		return userRepository.findByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
	}

	/**
	 * 회원정보 조회 메소드
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public UserInfoResponse getUserInfo(String userId) {
		
		UserInfo userInfo = userRepository.findByUserId(userId)
							.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
		
		return UserInfoResponse.from(userInfo);
	}
	
	
	/**
	 * 회원정보 수정 메소드
	 * @return
	 */
	@Transactional
	public void userUpdate(UserInfoUpdateRequest userInfoUpdateRequest, String userId) {
		
		// 현재 로그인 한 사용자를 찾음
		UserInfo userInfo = userRepository.findByUserId(userId)
							.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		
		// 이메일 중복 검사
		if (userRepository.existsByUserEmail(userInfoUpdateRequest.getUserEmail())) {
			throw new DuplicateDataException("이미 사용 중인 이메일입니다.");
		}
		
		userInfo.setUserNm(userInfoUpdateRequest.getUserNm());
		userInfo.setUserEmail(userInfoUpdateRequest.getUserEmail());
		
		//	@Transactional이 끝날 때, UserInfo 객체를 감지하여 자동으로 DB에 UPDATE 쿼리를 날리기 때문에 userRepository.save() 호출 불필요
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


	
}
