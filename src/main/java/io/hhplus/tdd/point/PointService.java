package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.NotExistUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

	private final PointRepository pointRepository;

	public UserPoint getPointByUserId(long id) {
		if(id<0) {
			throw new IllegalArgumentException("잘못된 사용자 아이디 입니다.");
		}

		UserPoint finduser = pointRepository.getPointByUserId(id);

		if(null == finduser) {
			throw new NotExistUserException();
		}

		return finduser;
	}
}
