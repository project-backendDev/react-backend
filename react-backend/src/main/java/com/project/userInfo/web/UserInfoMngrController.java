package com.project.userInfo.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.userInfo.service.UserInfoService;
import com.project.userInfo.vo.UserInfoResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mngr")
@RequiredArgsConstructor
public class UserInfoMngrController {

	private final UserInfoService userService;
	
	/**
	 * 회원정보 전체 리스트
	 * @return
	 */
	@GetMapping("/userList")
	public ResponseEntity<List<UserInfoResponse>> getAllUserList() {
		return ResponseEntity.ok(userService.getAllUserList());
	}
}
