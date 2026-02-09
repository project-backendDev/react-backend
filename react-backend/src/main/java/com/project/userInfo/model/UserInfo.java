package com.project.userInfo.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected
@AllArgsConstructor // 모든 필드를 포함한 생성자
@Builder // 👈 Builder 패턴 추가
@Table(name = "user_info") // DDL의 테이블 이름과 매핑
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 활성화
public class UserInfo implements UserDetails {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_SEQ") // DDL의 컬럼명과 매핑
    private Integer userSeq; // DDL에서 INT이므로 Integer (Long도 괜찮음)

	/* 아이디 */
    @Column(name = "USER_ID", nullable = false, unique = true, length = 20)
    private String userId;

    /* 비밀번호 */
    @Column(name = "USER_PW", nullable = false, length = 75)
    private String userPw;

    /* 이름 */
    @Column(name = "USER_NM", nullable = false, length = 45)
    private String userNm;

    /* 이메일 */
    @Column(name = "USER_EMAIL", nullable = false, unique = true, length = 100)
    private String userEmail;

    /* 권한 */
    @Column(name = "ROLE", nullable = false, length = 50)
    private String role;

    /* 로그인 타입 */
    @Column(name = "LOGIN_TYPE", nullable = false, length = 10)
    private String loginType;
    
    /* 계정상태 (활성 : Y / 비활성 : N) */
    @Column(name = "STATUS", nullable = false, length = 10)
    private String status;
    
    /* 생성일 */
    @CreatedDate // 엔티티 생성 시 자동 저장
    @Column(name = "REG_DATE", nullable = false, updatable = false)
    private Date regDate;

    /* 수정일 */
    @LastModifiedDate // 엔티티 수정 시 자동 저장
    @Column(name = "EDIT_DATE", nullable = false)
    private Date editDate;

    
    /**
     * 사용자의 권한(ROLE) 반환
     */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.role));
	}

	/**
	 * 사용자 아이디(USER_ID) 반환 -> Security에서는 'username'으로 통칭
	 */
	@Override
	public String getUsername() {
		return this.userId;
	}

	/**
	 * 사용자 비밀번호(USER_PW) 반환
	 */
	@Override
	public String getPassword() {
		return this.userPw;
	}

	
    @Override
    public boolean isAccountNonExpired() { return true; } // 계정 만료 여부
    
    @Override
    public boolean isAccountNonLocked() { return true; } // 계정 잠금 여부
    
    @Override
    public boolean isCredentialsNonExpired() { return true; } // 비밀번호 만료 여부
    
    @Override
    public boolean isEnabled() { return true; } // 계정 활성화 여부

    // 서비스에서 사용할 빌더 (JPA 기본 생성자 @NoArgsConstructor도 필요)
//    @Builder
//    public UserInfo(String userId, String userPw, String userNm, String userEmail, String role, String loginType) {
//        this.userId = userId;
//        this.userPw = userPw;
//        this.userNm = userNm;
//        this.userEmail = userEmail;
//        this.loginType = loginType;
//        this.role = role;
//    }
    
    /**
     * 관리자 페이지에서 사용자 정보 수정
     * @param userNm - 사용자 이름
     * @param userEmail - 사용자 이메일
     * @param role - 권한
     * @param status - 계정 활성화 상태 (Y/N)
     */
    public void adminUpdateUserInfo(String userNm, String userEmail, String role, String status) {
    	this.userNm = userNm;
    	this.userEmail = userEmail;
    	this.role = role;
    	this.status = status;
    	this.editDate = new Date();
    }
    
}
