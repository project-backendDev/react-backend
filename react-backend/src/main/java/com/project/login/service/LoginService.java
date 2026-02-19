package com.project.login.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.login.vo.LoginRequest;
import com.project.login.vo.LoginResponse;
import com.project.userInfo.service.UserInfoService;
import com.project.userLog.service.UserLoginLogService;
import com.project.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class LoginService {
	
	private final JwtUtil jwtUtil;
	
	private final AuthenticationManager authenticationManager;

	private final UserInfoService userInfoService;
	
	private final UserLoginLogService userLoginLogService;
	
	
	/**
	 * 로그인 메소드
	 * @param loginRequest
	 * @return
	 * @throws AuthenticationException
	 */
	@Transactional
	public LoginResponse loginProcess(LoginRequest loginRequest, HttpServletRequest request) throws AuthenticationException {
		
		String userId = loginRequest.getUserId();
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getHeader("X-Forwarded-For");
        
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
		try {
			// AuthenticationManager에게 "인증"을 요청
			// 이 과정에서 loadUserByUsername이 호출되고, 비밀번호 비교가 자동으로 일어남
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getUserId(),
							loginRequest.getUserPw()
							)
					);
			
			// 인증 성공 -> JwtUtil을 이용해 토큰 생성
			String token = jwtUtil.generateToken(authentication);
			
			// 로그인 성공 시 로그 기록
			userLoginLogService.saveLog(userId, ipAddress, userAgent, "Y", null);
			
			return new LoginResponse(token);
			
		} catch (DisabledException e) {
			userLoginLogService.saveLog(userId, ipAddress, userAgent, "N", "탈퇴/정지된 회원");
			
			throw new DisabledException("탈퇴한 회원입니다.");
		} catch (AuthenticationException e) {
			int currentFailCount = userInfoService.incrementFailCount(userId);
			
			if (currentFailCount >= 5) {
				userInfoService.lockUserAccount(userId);
				
				userLoginLogService.saveLog(userId, ipAddress, userAgent, "N", "비밀번호 5회 오류 (계정 잠금)");
				
				throw new LockedException("비밀번호를 5회 이상 불일치 하여 계정이 잠겼습니다. 관리자에게 문의하세요.");
			} else {
				userLoginLogService.saveLog(userId, ipAddress, userAgent, "N", "아이디 또는 비밀번호 불일치");
				
				throw new UsernameNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.");
			}
		} catch (Exception e) {
			userLoginLogService.saveLog(userId, ipAddress, userAgent, "N", "기타 로그인 시스템 오류");
			
			throw new BadCredentialsException("로그인 처리 중 오류가 발생했습니다.");
		}
	}


}
