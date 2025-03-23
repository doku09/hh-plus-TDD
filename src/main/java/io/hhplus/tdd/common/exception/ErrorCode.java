package io.hhplus.tdd.common.exception;

public enum ErrorCode {
	NEGATIVE_USER_ID("잘못된 사용자 아이디 입니다.");

	private String message;

	public String getMessage() {
		return this.message;
	}
	ErrorCode(String message) {
		this.message = message;
	}
}
