package com.project.login.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.login.service.LoginService;
import com.project.login.vo.LoginRequest;
import com.project.login.vo.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

	private final LoginService loginService;
	
	/**
	 * 로그인 API
	 */
	@PostMapping("/user/login")
	public ResponseEntity<LoginResponse> loginProcess(@Valid @RequestBody LoginRequest loginRequest,
													  HttpServletRequest request) {
		return ResponseEntity.ok(loginService.loginProcess(loginRequest, request));
	}
	
}
