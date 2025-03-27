package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.MaxPointException;
import io.hhplus.tdd.common.exception.NotEnoughPointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PointServiceIntegrationTest {

	@Autowired
	private PointService pointService;

	@Autowired
	private PointRepository pointRepository;


	@BeforeEach
	void 유저_생성() {
		pointService.charge(1L, 1000);
		pointService.charge(2L, 2000);
		pointService.charge(3L, 3000);
	}

	// ============== 포인트 충전 ================
	@Test
	@DisplayName("실패 - 사용자의 포인트가 최대 누적금액이 넘으면 예외를 던진다.")
	void max_amount() {

		// given
		long id = 1L;
		long amount = 20_000_000;

		// when && then
		assertThatThrownBy(() -> pointService.charge(id, amount))
			.isInstanceOf(MaxPointException.class);
	}

	@Test
	@DisplayName("성공 - 기존 사용자가 없으면 사용자를 추가하고 포인트를 충전한다.")
	void not_origin_user() {

		// given
		long id = 5L;
		long amount = 1000;

		// when
		UserPoint chargedUser = pointService.charge(id, amount);

		// then
		assertThat(chargedUser).isNotNull();
		assertThat(chargedUser.id()).isEqualTo(id);
		assertThat(chargedUser.point()).isEqualTo(1000);
	}

	@Test
	@DisplayName("성공 - 포인트를 충전하면 기존 사용자가 가지고 있던 포인트에 충전포인트를 더한다.")
	void origin_user() {

		// given
		long id = 10L; //새로운 번호
		long amount = 1000;

		// when
		pointService.charge(id, amount);

		UserPoint chargedUser = pointService.charge(id, amount);

		// then
		assertThat(chargedUser).isNotNull();
		assertThat(chargedUser.id()).isEqualTo(id);
		assertThat(chargedUser.point()).isEqualTo(2000);
	}

	// ============== 포인트 사용 ================
	@Test
	@DisplayName("실패 - 잔여 포인트가 사용할 포인트보다 적으면 실패한다.")
	void notEnoughPoint_fail() {

		// given
		long id = 5L;
		long amount = 10000L;

		// when && then
		assertThatThrownBy(() -> pointService.usePoint(id, amount))
			.isInstanceOf(NotEnoughPointException.class);
	}

	@Test
	@DisplayName("성공 - 포인트 사용 성공")
	void enoughPoint_success() {

		// given
		long id = 4L;
		long amount = 10000;
		long useAmount = 2000;

		// when

		// 4번 회원에게 10000원 충전
		pointService.charge(id, amount);

		// 4번 회원 2000원 사용
		UserPoint findUser = pointService.usePoint(id, useAmount);

		// then
		assertThat(findUser).isNotNull();
		assertThat(findUser.id()).isEqualTo(id);
		assertThat(findUser.point()).isEqualTo(amount - useAmount);
	}

	// ============== 포인트 내역 조회 ================
	@Test
	@DisplayName("성공 - 포인트 내역조회")
	void pointHistory_조회() {

		// given
		pointService.charge(1L, 10000);
		pointService.charge(1L, 3000);
		pointService.charge(1L, 4000);

		// when
		List<PointHistory> historyList = pointService.getHistoryByUserId(1L);

		// then
		assertThat(historyList).isNotEmpty();
		assertThat(historyList.size()).isEqualTo(4);
	}
}
