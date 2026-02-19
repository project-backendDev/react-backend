package com.project.userLog.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_login_log")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserLoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_SEQ")
    private Long logSeq;

    /* 아이디 */
    @Column(name = "USER_ID", length = 50)
    private String userId;

    /* 로그인 날짜 */
    @CreatedDate
    @Column(name = "LOGIN_DATE", updatable = false)
    private LocalDateTime loginDate;

    /* 접속 IP */
    @Column(name = "IP_ADDRESS", length = 50)
    private String ipAddress;

    /* 접속 기기 */
    @Column(name = "USER_AGENT", length = 500)
    private String userAgent;

    /* 접속 성공 여부 (성공 - Y / 실패 - N) */
    @Column(name = "IS_SUCCESS", length = 1, nullable = false)
    private String isSuccess;

    /* 실패 사유 */
    @Column(name = "FAIL_REASON", length = 100)
    private String failReason;
}