package com.project.userInfo.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserInfoUpdate {

	@NotBlank(message = "이름은 필수입니다.")
	private String userNm;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String userEmail;

	@NotBlank(message = "권한은 필수입니다.")
	private String role;

	@NotBlank(message = "상태는 필수입니다.")
	private String status; 
}
