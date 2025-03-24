package io.hhplus.tdd.point;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {

	public PointHistory(long userId, long amount, TransactionType type, long updateMillis) {
		this(0, userId, amount, type, updateMillis);
	}
}
