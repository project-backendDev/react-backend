package com.project.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.userInfo.model.UserInfo;

public interface LoginRepository extends JpaRepository<UserInfo, Integer> {

	
}
