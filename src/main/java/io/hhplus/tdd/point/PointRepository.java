package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepository {

	private final UserPointTable db;

	public UserPoint getPointByUserId(long id) {
		return db.selectById(id);
	}
}
