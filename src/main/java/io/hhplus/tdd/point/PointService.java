package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.ErrorMessage;
import io.hhplus.tdd.common.exception.MaxPointException;
import io.hhplus.tdd.common.exception.NegativeChargeAmountException;
import io.hhplus.tdd.common.exception.NotEnoughPointException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

	private final PointRepository pointRepository;

	public UserPoint getPointByUserId(long id) {
		if (id < 1) {
			throw new IllegalArgumentException(ErrorMessage.NEGATIVE_USER_ID.getMessage());
		}

		return pointRepository.getPointByUserId(id);
	}

	public UserPoint charge(long id, long amount) {

		if (id < 1) throw new IllegalArgumentException(ErrorMessage.NEGATIVE_USER_ID.getMessage());
		if (amount < 0) throw new NegativeChargeAmountException();

		UserPoint user = pointRepository.getPointByUserId(id);

		if (user.point() + amount > PointConstants.MAX_POINT) throw new MaxPointException();

		PointHistory historyRequest = new PointHistory(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

		pointRepository.insertHistory(historyRequest);

		return pointRepository.charge(user.id(), user.point() + amount);
	}

	public UserPoint usePoint(long id, long amount) {

		// Q) 사용자 아이디에 대한 validate가 반복되는데 validate 메서드가 어디에 위치해야 할까요?
		if (id < 1) {
			throw new IllegalArgumentException(ErrorMessage.NEGATIVE_USER_ID.getMessage());
		}
		if (amount < 0) {
			throw new IllegalArgumentException(ErrorMessage.NEGATIVE_AMOUNT.getMessage());
		}

		UserPoint findUser = pointRepository.getPointByUserId(id);

		if(findUser.point() - amount < 0) throw new NotEnoughPointException();

		PointHistory historyRequest = new PointHistory(id, amount, TransactionType.USE, System.currentTimeMillis());
		pointRepository.insertHistory(historyRequest);

		return pointRepository.usePoint(id, findUser.point() - amount);
	}

	public List<PointHistory> getHistoryByUserId(long id) {

		return pointRepository.getHistoryListByUserId(id);
	}
}
