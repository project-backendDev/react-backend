package com.project.exception;

public class DuplicateDataException extends RuntimeException {

	// 생성자에서 어떤 데이터가 중복되었는지 "메시지"를 받음
    public DuplicateDataException(String message) {
        super(message);
    }
}
