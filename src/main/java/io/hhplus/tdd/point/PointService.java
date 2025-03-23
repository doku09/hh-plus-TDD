package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.ErrorCode;
import io.hhplus.tdd.common.exception.MaxPointException;
import io.hhplus.tdd.common.exception.NegativeChargeAmountException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

	private final PointRepository pointRepository;

	public UserPoint getPointByUserId(long id) {
		if (id < 0) {
			throw new IllegalArgumentException("잘못된 사용자 아이디 입니다.");
		}

		return pointRepository.getPointByUserId(id);
	}

	public UserPoint charge(long id, long amount) {

		if (amount < 0) throw new NegativeChargeAmountException();
		if(id<0) throw new IllegalArgumentException(ErrorCode.NEGATIVE_USER_ID.getMessage());

		UserPoint user = pointRepository.getPointByUserId(id);

		if (user.point() + amount > PointConstants.MAX_POINT) throw new MaxPointException();

		return pointRepository.charge(user.id(), user.point() + amount);
	}
}
