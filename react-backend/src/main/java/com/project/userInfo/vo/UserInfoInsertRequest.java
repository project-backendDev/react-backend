package com.project.userInfo.vo;

import com.project.userInfo.model.UserInfo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoInsertRequest {

	@NotBlank(message = "아이디는 필수 입력 항목입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
	private String userId;
	
	@NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요. (영문, 특수문자 포함)")
	@Pattern(
	        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).*$", // (?=.*[0-9])` 추가
	        message = "비밀번호는 영문, 숫자, 특수문자(!@#$%^&*)를 최소 1자 이상 포함해야 합니다."
	    )
    private String userPw;
	
	@NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String userNm; 
	
	@NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
	@Pattern(
	        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", // .com 형태 검사
	        message = "올바른 이메일 형식이 아닙니다."
	    )
    private String userEmail;
    
	
	// DTO를 User 엔티티로 변환 (Builder 패턴 사용)
    public UserInfo toEntity() {
        return UserInfo.builder()
                .userId(userId)
                .userPw(userPw)
                .userNm(userNm)
                .userEmail(userEmail)
                .role("normal") 
                .loginType("site")
                .build();
    }
}
