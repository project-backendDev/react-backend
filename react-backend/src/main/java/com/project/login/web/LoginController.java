package com.project.login.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/api/user")
public class LoginController {

	private final LoginService loginService;
	
	/**
	 * 로그인 API
	 */
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> loginProcess(@Valid @RequestBody LoginRequest loginRequest,
													  HttpServletRequest request) {
		return ResponseEntity.ok(loginService.loginProcess(loginRequest, request));
	}

	/**
	 * SNS 로그인 API
	 * @param provider - SNS 구분 (카카오/네이버/구글)
	 * @param requestBody
	 * @param request
	 * @return
	 */
	@PostMapping("/oauth2/{provider}")
	public ResponseEntity<LoginResponse> snsLoginProcess(@PathVariable("provider") String provider,
														  @RequestBody Map<String, String> requestBody,
														  HttpServletRequest request) {
		System.out.println("SNS 로그인 컨트롤러");
		System.out.println("provider		::	" + provider);
		
		String code = requestBody.get("code");
		System.out.println("code		::	" + code);
		
		return ResponseEntity.ok(loginService.snsLoginProcess(provider, code, request));
	}
}
