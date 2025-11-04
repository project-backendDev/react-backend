package com.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	/**
     * @Valid 실패 시 (형식 오류)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 회원가입 중복 데이터
     * @return
     */
	@ExceptionHandler(DuplicateDataException.class) 
    public ResponseEntity<String> handleDuplicateData(DuplicateDataException e) {
		
        // 서비스에서 던진 메시지(e.getMessage())를 그대로 BAD_REQUEST로 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
	
	/**
     * 로그인 실패 (ID 없음, PW 틀림) 처리
     */
    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<String> handleAuthenticationException(Exception e) {
        // ID가 없거나, PW가 틀리면 401 Unauthorized 에러 반환
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED) 
                .body("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    /**
     * 그 외 모든 서버 내부 예외 (최종 안전망)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("서버 내부 오류가 발생했습니다.");
    }
}
