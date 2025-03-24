package io.hhplus.tdd.point;

public interface PointRepository {

	 UserPoint getPointByUserId(long id);

	 UserPoint charge(long id, long amount);

	 UserPoint usePoint(long id, long amount);
}
