package com.project.userInfo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.userInfo.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

	// 아이디 중복체크
	boolean existsByUserId(String userId);
	
	// 이메일 중복체크
    boolean existsByUserEmail(String userEmail);
    
    // 로그인 시 사용
    Optional<UserInfo> findByUserId(String userId);
    
}
