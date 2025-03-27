package io.hhplus.tdd.common.exception;

public enum ErrorMessage {
	NEGATIVE_USER_ID("잘못된 사용자 아이디 입니다."),
	NEGATIVE_AMOUNT("포인트는 0이상만 입력가능합니다.");

	private String message;

	public String getMessage() {
		return this.message;
	}
	ErrorMessage(String message) {
		this.message = message;
	}
}
