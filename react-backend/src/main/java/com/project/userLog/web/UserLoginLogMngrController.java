package com.project.userLog.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.cmm.PageRequestDto;
import com.project.userLog.model.UserLoginLog;
import com.project.userLog.service.UserLoginLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mngr")
@RequiredArgsConstructor
public class UserLoginLogMngrController {

	private final UserLoginLogService loginLogService;
	
	@GetMapping("/list")
    public ResponseEntity<Page<UserLoginLog>> getLogList(PageRequestDto pageRequest) {
        
        // 최신 로그가 맨 위에 오도록 역순 정렬 (logSeq 기준)
        Pageable pageable = pageRequest.getPageable(Sort.by("logSeq").descending());
        
        Page<UserLoginLog> logPage = loginLogService.getLogList(pageable);
        
        return ResponseEntity.ok(logPage);
    }
}
