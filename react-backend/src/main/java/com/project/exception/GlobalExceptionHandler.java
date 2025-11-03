package com.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateDataException.class) 
    public ResponseEntity<String> handleDuplicateData(DuplicateDataException e) {
		
        // 서비스에서 던진 메시지(e.getMessage())를 그대로 BAD_REQUEST로 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
