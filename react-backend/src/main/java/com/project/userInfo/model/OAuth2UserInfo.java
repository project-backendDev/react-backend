package com.project.userInfo.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserInfo {

	private String provider;
    private String providerId;
    private String nickname;
    private String email;
}
