package com.project.userInfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
	 * @param userRequest
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<String> userRegist(@Valid @RequestBody UserInfoRequest userRequest) {
		
		userService.userRegist(userRequest);
		
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
}
