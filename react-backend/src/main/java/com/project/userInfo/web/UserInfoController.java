package com.project.userInfo.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.userInfo.service.UserInfoService;
import com.project.userInfo.vo.UserInfoInsertRequest;
import com.project.userInfo.vo.UserInfoPwdCheckRequest;
import com.project.userInfo.vo.UserInfoResponse;
import com.project.userInfo.vo.UserInfoUpdateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserInfoController {
	
	private final UserInfoService userInfoService;
	
	/**
	 * 회원가입 API
	 * @param userInsertRequest
	 * @return
	 */
	@PostMapping("/user/signup")
	public ResponseEntity<String> userRegist(@Valid @RequestBody UserInfoInsertRequest userInsertRequest) {
		
		userInfoService.userRegist(userInsertRequest);
		
		// status 201 -> Created를 의미하며 클라이언트의 요청이 성공적으로 처리되었고, 그 결과 새로운 리소스가 생성되었음을 나타냄
		return ResponseEntity.status(201).body("회원가입이 완료되었습니다.");
	}
	
	/**
	 * 회원정보 페이지 전 비밀번호 체크 API
	 * @param userDetails
	 * @param userInfoPwdCheckRequest
	 * @return
	 */
	@PostMapping("/user/confirm-password")
	public ResponseEntity<String> confirmPassword(@AuthenticationPrincipal UserDetails userDetails,
												  @Valid @RequestBody UserInfoPwdCheckRequest userInfoPwdCheckRequest) {
		
		System.out.println("[S] Confirm Controller");
		System.out.println("userId		::	" + userDetails.getUsername());
		System.out.println("userPw		::	" + userInfoPwdCheckRequest.getUserPw());
	
		userInfoService.confirmPassword(userDetails.getUsername(), userInfoPwdCheckRequest.getUserPw());
		
		return ResponseEntity.ok("비밀번호 확인이 완료되었습니다.");
	}
	
	/**
	 * 회원정보 조회 API
	 * @param userDetails
	 * @return
	 */
	@GetMapping("/user/me")
	public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {

		// 인증된 사용자의 userId를 가져옴
		String userId = userDetails.getUsername();
		
		return ResponseEntity.ok(userInfoService.getUserInfo(userId));
	}
	
	/**
	 * 회원정보 수정 API
	 */
	@PutMapping("/user/me")
	public ResponseEntity<String> userUpdate(@AuthenticationPrincipal UserDetails userDetails, 
											 @Valid @RequestBody UserInfoUpdateRequest userInfoUpdateRequest) {
		
		// 인증된 사용자의 userId를 가져옴
		String userId = userDetails.getUsername();
		
		// 회원정보 수정
		userInfoService.userUpdate(userInfoUpdateRequest, userId);

		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
