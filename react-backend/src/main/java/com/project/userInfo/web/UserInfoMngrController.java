package com.project.userInfo.web;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.cmm.PageRequestDto;
import com.project.cmm.SearchRequestDto;
import com.project.userInfo.service.UserInfoService;
import com.project.userInfo.vo.AdminUserInfoDelete;
import com.project.userInfo.vo.AdminUserInfoDetail;
import com.project.userInfo.vo.AdminUserInfoUpdate;
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
	public ResponseEntity<Page<UserInfoResponse>> getAllUserList(SearchRequestDto search, PageRequestDto page) {
		return ResponseEntity.ok(userService.getAllUserList(search, page));
	}

	/**
	 * 단일 회원 정보 조회 메소드
	 * @param userId
	 * @return
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<AdminUserInfoDetail> getUserDetail(@PathVariable("userId") String userId) {
		return ResponseEntity.ok(userService.getUserDetail(userId));
	}
	
	/**
	 * 단일 회원 정보 수정 메소드
	 * @param userId
	 * @param adminUserInfoUpdate
	 * @return
	 */
	@PutMapping("/user/{userId}")
	public ResponseEntity<String> getUserUpdate(@PathVariable("userId") String userId, 
												@RequestBody AdminUserInfoUpdate adminUserInfoUpdate) {

		userService.adminUpdateUserInfo(userId, adminUserInfoUpdate);
		
		return ResponseEntity.ok("회원 정보가 수정되었습니다.");
	}
	
	/**
	 * 회원 탈퇴 메소드
	 * @param adminUserInfoDelete
	 * @return
	 */
	@DeleteMapping("/user")
	public ResponseEntity<String> userDelete(@RequestBody AdminUserInfoDelete adminUserInfoDelete) {
		
		userService.adminDeleteUserInfo(adminUserInfoDelete.getUserIds());
		
		return ResponseEntity.ok("선택한 회원이 탈퇴 처리되었습니다.");
	}
}
