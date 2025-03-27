package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointMapRepository implements PointRepository{

	private final UserPointTable pointDB;
	private final PointHistoryTable historyDB;

	@Override
	public UserPoint getPointByUserId(long id) {
		return pointDB.selectById(id);
	}

	@Override
	public UserPoint charge(long id, long amount) {
		return pointDB.insertOrUpdate(id,amount);
	}

	@Override
	public UserPoint usePoint(long id, long amount) {
		return pointDB.insertOrUpdate(id,amount);
	}

	@Override
	public void insertHistory(PointHistory history) {
		historyDB.insert(history.userId(), history.amount(), history.type(), history.updateMillis());
	}

	@Override
	public List<PointHistory> getHistoryListByUserId(long id) {
		return historyDB.selectAllByUserId(id);
	}
}
