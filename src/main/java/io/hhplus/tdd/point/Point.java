package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.MaxPointException;
import io.hhplus.tdd.common.exception.NegativeChargeAmountException;

public class Point {

    private Long point;

    private static final Long ZERO_POINT = 0L;

    public static Point of() {
        return new Point(ZERO_POINT);
    }

    public static Point of(long point) {
        if(point < 0) throw new NegativeChargeAmountException();
        if(point > PointConstants.MAX_POINT) throw new MaxPointException();
        return new Point(point);
    }

    public Point(long point) {
        this.point = point;
    }

    public Long getPoint() {
        return point;
    }
}
