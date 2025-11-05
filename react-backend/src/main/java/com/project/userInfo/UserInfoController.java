package com.project.userInfo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.login.LoginRequest;
import com.project.login.LoginResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserInfoController {
	
	private final UserInfoService userService;

	/**
	 * 회원가입 API
	 * @param userInsertRequest
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<String> userRegist(@Valid @RequestBody UserInfoInsertRequest userInsertRequest) {
		
		userService.userRegist(userInsertRequest);
		
		// status 201 -> Created를 의미하며 클라이언트의 요청이 성공적으로 처리되었고, 그 결과 새로운 리소스가 생성되었음을 나타냄
		return ResponseEntity.status(201).body("회원가입이 완료되었습니다.");
	}
	
	/**
	 * 로그인 API
	 */
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> loginProcess(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.loginProccess(loginRequest));
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
		
		return ResponseEntity.ok(userService.getUserInfo(userId));
	}
	
	/**
	 * 회원정보 수정 API
	 */
	@PutMapping("/user/me")
	public ResponseEntity<String> userUpdate(@AuthenticationPrincipal UserDetails userDetails, 
											 @Valid @RequestBody UserInfoUpdateRequest userInfoRequest) {
		
		// 인증된 사용자의 userId를 가져옴
		String userId = userDetails.getUsername();
		
		// 회원정보 수정
		userService.userUpdate(userInfoRequest, userId);

		return ResponseEntity.ok("회원정보가 수정되었습니다.");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
