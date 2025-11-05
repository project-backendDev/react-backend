package com.project.login.vo;

import lombok.Getter;

@Getter
public class LoginResponse {

	private String accessToken;
    private String tokenType = "Bearer";

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    
}
