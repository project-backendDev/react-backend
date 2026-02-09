package com.project.userInfo.vo;

import java.util.Date;

import com.project.userInfo.model.UserInfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
	private int userSeq;
	
    private String userId;
    
    private String userNm;
    
    private String userEmail;
    
    private String role;
    
    private String status;
    
    private Date regDate;
    
    
    public static UserInfoResponse from(UserInfo userInfo) {
        return UserInfoResponse.builder()
                .userSeq(userInfo.getUserSeq())
                .userId(userInfo.getUserId())
                .userNm(userInfo.getUserNm())
                .userEmail(userInfo.getUserEmail())
                .status(userInfo.getStatus())
                .role(userInfo.getRole())
                .regDate(userInfo.getRegDate())
                .build();
    }
}
