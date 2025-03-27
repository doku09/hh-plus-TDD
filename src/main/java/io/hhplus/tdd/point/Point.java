package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.MaxPointException;
import io.hhplus.tdd.common.exception.NegativeChargeAmountException;
import io.hhplus.tdd.common.exception.NotEnoughPointException;

public class Point {

    private Long point;

    private static final Long ZERO_POINT = 0L;
    private static final Long MAX_POINT = 1_000_000L;

    public Point(long point) {
        this.point = point;
    }

    public Long getPoint() {
        return point;
    }

    public void charge(long amount) {
        if(amount < 0) throw new NegativeChargeAmountException();
        if(this.point + amount > MAX_POINT) throw new MaxPointException();
        this.point += amount;
    }

    public void use(Long point) {
        if(this.point - point < 0) throw new NotEnoughPointException();
        this.point -= point;
    }

    public static Point of() {
        return new Point(ZERO_POINT);
    }

    public static Point of(long point) {
        if(point < 0) throw new NegativeChargeAmountException();
        if(point > MAX_POINT) throw new MaxPointException();
        return new Point(point);
    }
}
