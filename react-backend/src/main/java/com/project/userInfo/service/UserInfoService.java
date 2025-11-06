package com.project.userInfo.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
@RequiredArgsConstructor
public class UserInfoService {

	private final UserInfoRepository userInfoRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	/**
	 * 회원가입 메소드
	 */
	@Transactional
	public void userRegist(UserInfoInsertRequest userInfoInsertRequest) {
		
		// 아이디 중복검사
		if (userInfoRepository.existsByUserId(userInfoInsertRequest.getUserId())) {
			throw new DuplicateDataException("이미 사용 중인 아이디입니다.");
		}
		
		// 이메일 중복검사
		if (userInfoRepository.existsByUserEmail(userInfoInsertRequest.getUserEmail())) {
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
						.status("Y")
						.build();
						
		// 생성된 엔티티를 저장
		userInfoRepository.save(userInfo);
	}

	
	/**
	 * 회원정보수정 페이지 넘어가기 전 비밀번호가 일치하는지 체크하는 매소드
	 * @param userId
	 * @param userPw
	 */
	@Transactional(readOnly = true)
	public void confirmPassword(String userId, String userPw) {
		
		// DB에서 현재 사용자 정보 획득
		UserInfo userInfo = userInfoRepository.findByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
		
		// DB에 저장된 비밀번호와 입력된 비밀번호를 비교
		if (!passwordEncoder.matches(userPw, userInfo.getUserPw())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
	}

	/**
	 * 회원정보 조회 메소드
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = true)
	public UserInfoResponse getUserInfo(String userId) {
		
		UserInfo userInfo = userInfoRepository.findByUserId(userId)
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
		UserInfo userInfo = userInfoRepository.findByUserId(userId)
							.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		
		// 이메일 중복 검사
		// 회원 수정할 때에는 중복검사가 필요할까..?
//		if (userInfoRepository.existsByUserEmail(userInfoUpdateRequest.getUserEmail())) {
//			throw new DuplicateDataException("이미 사용 중인 이메일입니다.");
//		}
		
		userInfo.setUserNm(userInfoUpdateRequest.getUserNm());
		userInfo.setUserEmail(userInfoUpdateRequest.getUserEmail());
		
		//	@Transactional이 끝날 때, UserInfo 객체를 감지하여 자동으로 DB에 UPDATE 쿼리를 날리기 때문에 userRepository.save() 호출 불필요
	}
	
	/**
	 * 회원탈퇴 메소드
	 * STATUS를 Y -> N 으로 변경
	 * @param userId
	 */
	@Transactional
	public void userDelete(String userId) {
		
		// 현재 로그인 한 사용자를 찾음
		UserInfo userInfo = userInfoRepository.findByUserId(userId)
							.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		String randomPassword = UUID.randomUUID().toString();
		System.out.println("PWD");
		System.out.println(randomPassword);
		String encPassword = passwordEncoder.encode(randomPassword);
		String anonyEmail = userInfo.getUserSeq() + "@delete.com";
		
		userInfo.setUserNm("탈퇴회원");
		userInfo.setUserPw(encPassword);
		userInfo.setUserEmail(anonyEmail);
		userInfo.setStatus("N");
	}
	
	/*
	 * [관리자]
	 * 전체 회원 조회 메소드
	 */
	public List<UserInfoResponse> getAllUserList() {
		return userInfoRepository.findAll()
					.stream()
					.map(UserInfoResponse::from)
					.collect(Collectors.toList());
	}


	
}
