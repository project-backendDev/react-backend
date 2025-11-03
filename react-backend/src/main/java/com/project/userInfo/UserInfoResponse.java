package com.project.userInfo;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
	private int userSeq;
	
    private String userId;
    
    private String userNm;
    
    private String userEmail;
    
    private Date regDate;
    
    public static UserInfoResponse from(UserInfo userInfo) {
        return UserInfoResponse.builder()
                .userSeq(userInfo.getUserSeq())
                .userId(userInfo.getUserId())
                .userNm(userInfo.getUserNm())
                .userEmail(userInfo.getUserEmail())
                .regDate(userInfo.getRegDate())
                .build();
    }
}
