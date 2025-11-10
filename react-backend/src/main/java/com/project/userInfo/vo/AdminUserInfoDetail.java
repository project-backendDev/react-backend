package com.project.userInfo.vo;

import java.util.Date;

import com.project.userInfo.model.UserInfo;

import lombok.Getter;

@Getter
public class AdminUserInfoDetail {

	private Integer userSeq;
    private String userId;
    private String userNm;
    private String userEmail;
    private String role;
    private String loginType;
    private String status;
    private Date regDate;
    private Date editDate;
    
    public AdminUserInfoDetail(UserInfo userInfo) {
        this.userSeq = userInfo.getUserSeq();
        this.userId = userInfo.getUserId();
        this.userNm = userInfo.getUserNm();
        this.userEmail = userInfo.getUserEmail();
        this.role = userInfo.getRole();
        this.loginType = userInfo.getLoginType();
        this.status = userInfo.getStatus();
        this.regDate = userInfo.getRegDate();
        this.editDate = userInfo.getEditDate();
    }
}
