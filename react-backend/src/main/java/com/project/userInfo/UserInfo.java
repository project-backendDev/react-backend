package com.project.userInfo;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected
@AllArgsConstructor // 모든 필드를 포함한 생성자
@Builder // 👈 Builder 패턴 추가
@Table(name = "user_info") // DDL의 테이블 이름과 매핑
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 활성화
public class UserInfo {

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
    
    /* 생성일 */
    @CreatedDate // 엔티티 생성 시 자동 저장
    @Column(name = "REG_DATE", nullable = false, updatable = false)
    private Date regDate;

    /* 수정일 */
    @LastModifiedDate // 엔티티 수정 시 자동 저장
    @Column(name = "EDIT_DATE", nullable = false)
    private Date editDate;

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
    
}
