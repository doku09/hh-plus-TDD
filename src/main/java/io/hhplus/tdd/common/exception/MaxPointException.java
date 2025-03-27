package io.hhplus.tdd.common.exception;

import io.hhplus.tdd.point.PointConstants;

public class MaxPointException extends RuntimeException {
	public MaxPointException() {
		super("충전 포인트는 최대" + PointConstants.MAX_POINT +"원 까지 가능합니다.");
	}
}
