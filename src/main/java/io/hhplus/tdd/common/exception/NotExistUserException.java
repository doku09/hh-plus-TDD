package io.hhplus.tdd.common.exception;

public class NotExistUserException extends RuntimeException {

	public NotExistUserException() {
		super("사용자 정보가 존재하지 않습니다.");
	}
}
