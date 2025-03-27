package io.hhplus.tdd.common.exception;

public class NegativeChargeAmountException extends RuntimeException{
	public NegativeChargeAmountException() {
		super("충전할 금액은 음수가 될 수 없습니다.");
	}
}
