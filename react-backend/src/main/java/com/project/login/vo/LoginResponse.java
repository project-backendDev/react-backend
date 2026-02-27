package com.project.login.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	private String accessToken;
	
    private String tokenType = "Bearer";
    
    private String loginType;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public LoginResponse(String accessToken, String loginType) {
        this.accessToken = accessToken;
        this.loginType = loginType;
    }
}
