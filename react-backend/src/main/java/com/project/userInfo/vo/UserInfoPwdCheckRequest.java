package com.project.userInfo.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoPwdCheckRequest {

	@NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
	private String userPw;
}
