package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointMapRepository implements PointRepository{

	private final UserPointTable db;

	public UserPoint getPointByUserId(long id) {
		return db.selectById(id);
	}

	public UserPoint charge(long id, long amount) {
		return db.insertOrUpdate(id,amount);
	}

	public UserPoint usePoint(long id, long amount) {
		return db.insertOrUpdate(id,amount);
	}
}
