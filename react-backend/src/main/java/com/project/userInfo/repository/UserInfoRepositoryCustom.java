package com.project.userInfo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.cmm.SearchRequestDto;
import com.project.userInfo.model.UserInfo;

public interface UserInfoRepositoryCustom {

	Page<UserInfo> getUserList(SearchRequestDto searchDto, Pageable pageable);
	
}
