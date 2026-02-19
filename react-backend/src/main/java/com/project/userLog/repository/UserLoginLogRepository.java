package com.project.userLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.userLog.model.UserLoginLog;

public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {

}
