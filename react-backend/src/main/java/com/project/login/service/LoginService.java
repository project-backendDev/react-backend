package com.project.login.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.login.vo.LoginRequest;
import com.project.login.vo.LoginResponse;
import com.project.userInfo.model.OAuth2UserInfo;
import com.project.userInfo.model.UserInfo;
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
	
	private final PasswordEncoder passwordEncoder; 

	@Value("${kakao.client.id}")
	private String kakaoClientId;

	@Value("${kakao.redirect.uri}")
	private String kakaoRedirectUri;
	
	@Value("${naver.client.id}")
	private String naverClientId;
	
	@Value("${naver.client.secret}")
	private String naverClientSecret;
	
	/**
	 * 일반 로그인 메소드
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
			
			return new LoginResponse(token, "SITE");
			
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

	/**
	 * SNS 로그인 처리 프로세스
	 * @param provider
	 * @param code
	 * @param request
	 * @return
	 */
	@Transactional
	public LoginResponse snsLoginProcess(String provider, String code, HttpServletRequest request) {
		try {
			// 소셜 유저 정보 가져오기
			OAuth2UserInfo userInfo = getSocialUserInfo(provider, code);
			
			// DB에 저장할 아이디 생성
			String userId = provider + "_" + userInfo.getProviderId();
			
			checkAndRegisterSocialUser(userId, userInfo);

			// 로그인 처리 및 JWT 토큰 생성
			Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, null); 
			String token = jwtUtil.generateToken(authentication);

			// 로그인 로그
			String ipAddress = request.getHeader("X-Forwarded-For");
			if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
			}
			String userAgent = request.getHeader("User-Agent");
			
			userLoginLogService.saveLog(userId, ipAddress, userAgent, "Y", provider.toUpperCase() + " 소셜 로그인 성공");

			return new LoginResponse(token, provider.toUpperCase());

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadCredentialsException(provider + " 로그인 처리 중 오류가 발생했습니다.");
		}
	}
	
	
	/**
	 * SNS 타입에 맞게 데이터를 가져오는 메소드
	 * @param provider
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private OAuth2UserInfo getSocialUserInfo(String provider, String code) throws Exception {
		
		String accessToken = getSocialAccessToken(provider, code);
		JsonNode jsonNode = getSocialProfile(provider, accessToken);
		
		if ("kakao".equalsIgnoreCase(provider)) {
			String email = null;
			if (jsonNode.get("kakao_account").has("email") && !jsonNode.get("kakao_account").get("email").isNull()) {
				email = jsonNode.get("kakao_account").get("email").asText();
			}
			return OAuth2UserInfo.builder()
					.provider("kakao")
					.providerId(jsonNode.get("id").asText())
					.nickname(jsonNode.get("properties").get("nickname").asText())
					.email(email)
					.build();
					
		} else if ("naver".equalsIgnoreCase(provider)) {
			JsonNode responseNode = jsonNode.get("response");
			
			return OAuth2UserInfo.builder()
					.provider("naver")
					.providerId(responseNode.get("id").asText())
					.nickname(responseNode.get("nickname").asText())
					.email(responseNode.has("email") ? responseNode.get("email").asText() : null)
					.build();

		} else if ("google".equalsIgnoreCase(provider)) {
			return OAuth2UserInfo.builder()
					.provider("google")
					.providerId(jsonNode.get("id").asText())
					.nickname(jsonNode.get("name").asText()) 
					.email(jsonNode.has("email") ? jsonNode.get("email").asText() : null)
					.build();
		} else {
			throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
		}
	}
	
	/**
	 * SNS 로그인 가입여부 확인 및 가입
	 */
	private void checkAndRegisterSocialUser(String userId, OAuth2UserInfo oAuth2UserInfo) {
		boolean isExist = userInfoService.existsByUserId(userId); 
		
		if (!isExist) {
			String randomPassword = UUID.randomUUID().toString();
			String encodedPassword = passwordEncoder.encode(randomPassword);
			
			UserInfo userInfo = UserInfo.builder()
					.userId(userId)
					.userPw(encodedPassword)
					.userNm(oAuth2UserInfo.getNickname()) // 카카오에서 받아온 닉네임
					.userEmail(oAuth2UserInfo.getEmail()) // 이메일 (없으면 null 들어감)
					.role("ROLE_USER")
					.loginType(oAuth2UserInfo.getProvider().toUpperCase()) // KAKAO, NAVER 등으로 들어감
					.status("Y")
					.build();
			
			userInfoService.saveSocialUser(userInfo);
		}
	}
	
	/**
	 * SNS 로그인 시 액세스 토큰 요청 통합 메소드
	 * @param provider
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private String getSocialAccessToken(String provider, String code) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// 공통 파라미터
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("code", code);

		String tokenUrl = "";

		// SNS별 분기 처리 (URL 및 전용 파라미터 추가)
		if ("kakao".equalsIgnoreCase(provider)) {
			tokenUrl = "https://kauth.kakao.com/oauth/token";
			params.add("client_id", kakaoClientId);
			params.add("redirect_uri", kakaoRedirectUri);
			
		} else if ("naver".equalsIgnoreCase(provider)) {
			tokenUrl = "https://nid.naver.com/oauth2.0/token";
			params.add("client_id", naverClientId);
			params.add("client_secret", naverClientSecret);
			params.add("state", UUID.randomUUID().toString()); // 네이버 필수
			
//		} else if ("google".equalsIgnoreCase(provider)) {
//			tokenUrl = "https://oauth2.googleapis.com/token";
//			params.add("client_id", googleClientId);
//			params.add("client_secret", googleClientSecret);
//			params.add("redirect_uri", googleRedirectUri); // 구글 필수
		}

		HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, tokenRequest, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(response.getBody());
		
		return jsonNode.get("access_token").asText();
	}
	
	/**
	 * SNS 로그인 시 사용자 정보 요청 통합 메소드
	 * @param provider
	 * @param accessToken
	 * @return
	 * @throws Exception
	 */
	private JsonNode getSocialProfile(String provider, String accessToken) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		String profileUrl = "";
		HttpMethod method = HttpMethod.GET; // 네이버, 구글은 GET이 기본

		// SNS별 분기 처리
		if ("kakao".equalsIgnoreCase(provider)) {
			profileUrl = "https://kapi.kakao.com/v2/user/me";
			method = HttpMethod.POST; // 카카오는 POST 사용
			headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			
		} else if ("naver".equalsIgnoreCase(provider)) {
			profileUrl = "https://openapi.naver.com/v1/nid/me";
			
		} else if ("google".equalsIgnoreCase(provider)) {
			profileUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
		}

		HttpEntity<MultiValueMap<String, String>> profileRequest = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(profileUrl, method, profileRequest, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		
		return objectMapper.readTree(response.getBody());
	}
	
	
	/**
	 * 26.03.04 SNS 로그인 시 데이터를 가져오는 부분을 통합하기 위해서 주석처리 
	 * @param provider
	 * @param code
	 * @return
	 * @throws Exception
	 */
	/*
	private OAuth2UserInfo getSocialUserInfo(String provider, String code) throws Exception {
		if ("kakao".equalsIgnoreCase(provider)) {
			// 카카오 토큰 및 정보 요청 로직
			String accessToken = getKakaoAccessToken(code);
			JsonNode jsonNode = getKakaoProfile(accessToken);
			
			String email = null;
			if (jsonNode.get("kakao_account").has("email") && !jsonNode.get("kakao_account").get("email").isNull()) {
				email = jsonNode.get("kakao_account").get("email").asText();
			}
			
			return OAuth2UserInfo.builder()
					.provider("kakao")
					.providerId(jsonNode.get("id").asText())
					.nickname(jsonNode.get("properties").get("nickname").asText())
					.email(email)
					.build();
					
		} else if ("naver".equalsIgnoreCase(provider)) {
			// TODO: 나중에 네이버 통신 로직 추가
			return null;
		} else if ("google".equalsIgnoreCase(provider)) {
			// TODO: 나중에 구글 통신 로직 추가
			return null;
		} else {
			throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
		}
	}
	*/
	
	
	/**
	 * 26.03.04 SNS 로그인 시 데이터를 가져오는 부분을 통합하기 위해서 주석처리
	 * 카카오 로그인 시 토큰 가져오기
	 * @param code
	 * @return
	 * @throws Exception
	 */
	/*
	private String getKakaoAccessToken(String code) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", kakaoClientId);
		params.add("redirect_uri", kakaoRedirectUri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);
		ResponseEntity<String> response = restTemplate.exchange(
												"https://kauth.kakao.com/oauth/token",
												HttpMethod.POST,
												tokenRequest,
												String.class
											);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(response.getBody());
		
		return jsonNode.get("access_token").asText();
	}
	*/
	
	

	/**
	 * 26.03.04 SNS 로그인 시 데이터를 가져오는 부분을 통합하기 위해서 주석처리
	 * 카카오 로그인 시 사용자 정보 요청
	 */
	/*
	private JsonNode getKakaoProfile(String accessToken) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		HttpEntity<MultiValueMap<String, String>> profileRequest = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
												"https://kapi.kakao.com/v2/user/me",
												HttpMethod.POST,
												profileRequest,
												String.class
											);

		ObjectMapper objectMapper = new ObjectMapper();
		
		return objectMapper.readTree(response.getBody());
	}
	*/
}
