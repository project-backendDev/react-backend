package com.project.userLog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.userLog.model.UserLoginLog;
import com.project.userLog.repository.UserLoginLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLoginLogService {

	private final UserLoginLogRepository loginLogRepository;
	
	/**
	 * [사용자] 로그인 로그 저장
	 * @param userId
	 * @param ipAddress
	 * @param userAgent
	 * @param isSuccess
	 * @param failReason
	 */
	@Transactional
	public void saveLog(String userId, String ipAddress, String userAgent, String isSuccess, String failReason) {
		UserLoginLog log = UserLoginLog.builder()
                .userId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isSuccess(isSuccess) 
                .failReason(failReason) 
                .build();
        
        loginLogRepository.save(log);
	}
	
	/**
	 * [관리자] 로그인 로그 이력
	 * @param pageable
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<UserLoginLog> getLogList(Pageable pageable) {
        return loginLogRepository.findAll(pageable);
    }
}
