package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PointService {

	private final PointRepository pointRepository;
	//유저별로 락을 만들어서 A와 B가 동시에 접근했을 때는 허용하도록 한다.
	private final Map<Long, ReentrantLock> userLockMap = new ConcurrentHashMap<>();

	public UserPoint getPointByUserId(long id) {
		if (id < 1) {
			throw new IllegalArgumentException(ErrorMessage.NEGATIVE_USER_ID.getMessage());
		}

		return pointRepository.getPointByUserId(id);
	}

	public UserPoint charge(long id, long amount) {

		if (id < 1) throw new IllegalArgumentException(ErrorMessage.NEGATIVE_USER_ID.getMessage());
		System.out.println("[" + System.currentTimeMillis() + "]" + Thread.currentThread().getName());

		ReentrantLock lock = userLockMap.computeIfAbsent(id, k -> new ReentrantLock()); // 키가 없으면 만들고 바로 리턴

		lock.lock();
		try {
			UserPoint user = pointRepository.getPointByUserId(id);

			Point point = Point.of(user.point());

			point.charge(amount);

			PointHistory historyRequest = new PointHistory(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

			pointRepository.insertHistory(historyRequest);

			return pointRepository.charge(user.id(), point.getPoint());
		} finally {
			lock.unlock();
		}
	}

	public UserPoint usePoint(long id, long amount) {

		// Q) 사용자 아이디에 대한 validate가 반복되는데 validate 메서드가 어디에 위치해야 할까요?
		if (id < 1) {
			throw new IllegalArgumentException(ErrorMessage.NEGATIVE_USER_ID.getMessage());
		}
		System.out.println("[" + System.currentTimeMillis() + "]" + Thread.currentThread().getName());

		ReentrantLock lock = userLockMap.computeIfAbsent(id, k -> new ReentrantLock());
		lock.lock();

		try {
			UserPoint findUser = pointRepository.getPointByUserId(id);

			Point point = Point.of(findUser.point());
			point.use(amount);

			PointHistory historyRequest = new PointHistory(id, amount, TransactionType.USE, System.currentTimeMillis());
			pointRepository.insertHistory(historyRequest);

			return pointRepository.usePoint(id, point.getPoint());
		} finally {
			lock.unlock();
		}
	}

	public List<PointHistory> getHistoryByUserId(long id) {

		return pointRepository.getHistoryListByUserId(id);
	}
}
