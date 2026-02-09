package com.project.userInfo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.userInfo.model.UserInfo;
import com.project.userInfo.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInfoCustomDetailService implements UserDetailsService {
	
	private final UserInfoRepository userInfoRepository;

	/**
     * Spring Security가 "신분증(UserDetails)"을 요청할 때 호출
     */
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		
		UserInfo userInfo =  userInfoRepository.findByUserId(userId)
							.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
		
//		boolean isStatus = userInfo.getStatus().equals("Y");
		boolean isStatus = userInfo.getStatus().equals("Y");
		
		// 권한 정보를 담을 리스트
		List<GrantedAuthority> authorities = new ArrayList<>();
		// 권한 추가 (SimpleGrantedAuthority를 사용해서 Spring Security가 이해할 수 있는 데이터로 바꿔줌)
		authorities.add(new SimpleGrantedAuthority(userInfo.getRole()));
		
		return new User(
				userInfo.getUserId(),
				userInfo.getUserPw(),
				isStatus,
				true,
				true,
				true,
				authorities
				);
	}
}
