package com.project.userInfo.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.userInfo.service.UserInfoService;
import com.project.userInfo.vo.AdminUserInfoDetail;
import com.project.userInfo.vo.UserInfoResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mngr")
@RequiredArgsConstructor
public class UserInfoMngrController {

	private final UserInfoService userService;
	
	/**
	 * 전체 회원정보 조회 메소드
	 * @return
	 */
	@GetMapping("/user-list")
	public ResponseEntity<List<UserInfoResponse>> getAllUserList() {
		return ResponseEntity.ok(userService.getAllUserList());
	}

	/**
	 * 단일 회원 정보 조회 메소드
	 * @param userId
	 * @return
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<AdminUserInfoDetail> getUserDetail(@PathVariable String userId) {
		return ResponseEntity.ok(userService.getUserDetail(userId));
	}
}
