package io.hhplus.tdd.point;

import java.util.List;

public interface PointRepository {

	 UserPoint getPointByUserId(long id);

	 UserPoint charge(long id, long amount);

	 UserPoint usePoint(long id, long amount);

	 void insertHistory(PointHistory history);

	List<PointHistory> getHistoryListByUserId(long id);
}
