package com.project.userInfo.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.cmm.PageRequestDto;
import com.project.cmm.SearchRequestDto;
import com.project.exception.DuplicateDataException;
import com.project.userInfo.model.UserInfo;
import com.project.userInfo.repository.UserInfoRepository;
import com.project.userInfo.vo.AdminUserInfoDetail;
import com.project.userInfo.vo.AdminUserInfoUpdate;
import com.project.userInfo.vo.UserInfoInsertRequest;
import com.project.userInfo.vo.UserInfoResponse;
import com.project.userInfo.vo.UserInfoUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
	public void confirmPassword(String userId, String userPw) {
		
		// 회원 존재여부 확인
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
	public UserInfoResponse getUserInfo(String userId) {
		// 회원 존재여부 확인
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
		
		// 회원 존재여부 확인
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
		
		// 회원 존재여부 확인
		UserInfo userInfo = userInfoRepository.findByUserId(userId)
							.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		String randomPassword = UUID.randomUUID().toString();
		String encPassword = passwordEncoder.encode(randomPassword);
		String anonyEmail = userInfo.getUserSeq() + "@delete.com";
		
		userInfo.setUserNm("탈퇴회원");
		userInfo.setUserPw(encPassword);
		userInfo.setUserEmail(anonyEmail);
		userInfo.setStatus("N");
		userInfo.setWithdrawDate(new Date());
	}
	
	/*
	 * [관리자]
	 * 전체 회원 조회 메소드
	 * 25.12.22
	 * 페이징 처리 된 형태로 다시 작성
	 */
//	public List<UserInfoResponse> getAllUserList() {
//		return userInfoRepository.findAll()
//					.stream()
//					.map(UserInfoResponse::from)
//					.collect(Collectors.toList());
//	}
	
	public Page<UserInfoResponse> getAllUserList(SearchRequestDto searchParam, PageRequestDto pageParam) {
		
		Pageable page = pageParam.getPageable(Sort.by("regDate").descending());
		
		// 이대로 관리자 페이지에서 user-list를 호출하면 아래 오류가 뜸
		// Serializing PageImpl instances as-is is not supported, meaning that there is no guarantee about the stability of the resulting JSON structure!
		// 안전성 보장이 되지 않는다고 해서 뜨는 오류여서 application.properties 파일에 VIA_DTO 설정을 추가함
		Page<UserInfo> resultPage = userInfoRepository.getUserList(searchParam, page);
		
		return resultPage.map(UserInfoResponse::from);
	}
	

	/**
	 * [관리자] 단일 회원 조회 메소드
	 * @param userId
	 * @return
	 */
	public AdminUserInfoDetail getUserDetail(String userId) {
		// 회원 존재여부 확인
		UserInfo userInfo = userInfoRepository.findByUserId(userId)
							.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		
		return new AdminUserInfoDetail(userInfo);
	}
	
	/**
	 * [관리자] 단일 회원 정보수정
	 */
	@Transactional
	public void adminUpdateUserInfo(String userId, AdminUserInfoUpdate adminUserInfoUpdate) {
		// 회원 존재여부 확인
		UserInfo userInfo = userInfoRepository.findByUserId(userId)
							.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		
		userInfo.adminUpdateUserInfo(adminUserInfoUpdate.getUserNm()
								   , adminUserInfoUpdate.getUserEmail()
								   , adminUserInfoUpdate.getRole()
								   , adminUserInfoUpdate.getStatus());
	}
	
	/**
	 * [관리자] (단건/다건) 회원 정보삭제
	 * @param userId
	 */
	@Transactional
	public void adminDeleteUserInfo(List<String> userIds) {
		
		if (userIds == null || userIds.isEmpty()) {
			return;
		}
		
		// 체크된 id의 유저를 조회
		List<UserInfo> userInfoList = userInfoRepository.findByUserIdIn(userIds);
		
		// randomPassword와 encPassword를 for문에 넣으면 대량 데이터 실행 시 성능이슈가 생길것 같아 밖으로 빼냄
		String randomPassword = UUID.randomUUID().toString();
		String encPassword = passwordEncoder.encode(randomPassword);
		
		// 체크된 유저 리스트를 for문 돌려서 탈퇴회원으로 변경
		for (UserInfo userInfo : userInfoList) {
			String anonyEmail = userInfo.getUserSeq() + "@delete.com";
			
			userInfo.setUserNm("탈퇴회원");
			userInfo.setUserPw(encPassword);
			userInfo.setUserEmail(anonyEmail);
			userInfo.setStatus("N");
		}
		
		
	}
}
