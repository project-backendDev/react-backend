package com.project.util;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

	@Value("${jwt.secretKey}")
    private String secretKey;
	
    @Value("${jwt.expiration}")
    private long expirationTime;
    
    private Key key; // 암호화된 비밀 키
    
    private JwtParser jwtParser; // 토큰 파싱(해석)기
    
    @PostConstruct		// 객체(Bean)가 생성된 직후에 이 메서드를 실행
    public void init() {
        // 1. secretKey를 바이트 배열로 변환
        byte[] keyBytes = secretKey.getBytes();
        // 2. HS256 알고리즘을 사용하는 암호화 키(Key) 객체 생성
        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        // 3. 이 키를 사용하는 JwtParser 생성
        this.jwtParser = Jwts.parser().setSigningKey(key).build();
    }
    
    /**
     * 인증(Authentication) 객체를 받아 JWT 토큰을 생성
     * @param authentication (사용자 ID, 권한 정보가 담겨있음)
     * @return String (JWT 토큰)
     */
    public String generateToken(Authentication authentication) {
        // 1. 권한 정보(ROLE_USER, ROLE_ADMIN)를 쉼표로 구분된 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 2. 토큰 생성
        return Jwts.builder()
                .setSubject(authentication.getName()) // 👈 토큰 제목 (사용자 ID)
                .claim("auth", authorities) // 👈 "auth"라는 이름으로 권한 정보 저장
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 키와 알고리즘
                .compact(); // 문자열로 변환
    }
    
    /**
     * 토큰에서 사용자 이름(userId) 추출
     */
    public String extractUsername(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }
    
    /**
     * 토큰 유효성 검사 (만료 시간, 서명 등)
     */
    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않은 경우 (위조, 만료 등)
            return false;
        }
    }
}
