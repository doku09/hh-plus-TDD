package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.MaxPointException;
import io.hhplus.tdd.common.exception.NegativeChargeAmountException;
import io.hhplus.tdd.common.exception.NotEnoughPointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class PointTest {

	@Test
	@DisplayName("포인트를 생성합니다.")
	void createPointTest() {

		// when
		Point point = Point.of();

		// then
		assertThat(point.getPoint()).isEqualTo(0);
	}

	@Test
	@DisplayName("포인트에 음수가 할당되면 예외를 던집니다.")
	void givenMinusValue_whenPointIsCharged_thenExceptionIsThrown() {

		//when & then
		assertThatThrownBy(() -> Point.of(-1L))
			.isInstanceOf(NegativeChargeAmountException.class);
	}

	@DisplayName("최대 포인트 초과 시 예외를 던집니다.")
	@ParameterizedTest
	@ValueSource(longs = {PointConstants.MAX_POINT + 1, PointConstants.MAX_POINT + 1000})
	void MaxAmount_Charge_ThrowException(Long point) {

		assertThatThrownBy(() -> Point.of(point))
			.isInstanceOf(MaxPointException.class);
	}

	@DisplayName("최대 포인트 이하면 예외를 던지지 않습니다.")
	@ParameterizedTest
	@ValueSource(longs = {1_000_000, 1_000_000 - 1})
	void ValidAmount_CreatePoint_Success(Long point) {

		// when & then
		assertThatCode(() -> Point.of(point))
			.doesNotThrowAnyException();
	}

	@DisplayName("기존사용자의 포인트에 충전할 포인트를 더했을때 최대 포인트 초과 시 예외를 던집니다.")
	@Test
	void originPointPlusChargePoint_maxPoint_throwException() {

		// given
		long originUserPoint = 1_000_000;
		long chargePoint = 2000L;

		// when
		Point point = Point.of(originUserPoint);

		// then
		assertThatThrownBy(() -> point.charge(chargePoint))
			.isInstanceOf(MaxPointException.class);
	}

	@Test
	@DisplayName("기존사용자의 포인트에 충전포인트를 충전하면 정상적으로 더해진다.")
	void originPOintChargePoint_success() {

		// given
		Point origionPoint = Point.of(1000);

		// when
		origionPoint.charge(2000);

		// then
		assertThat(origionPoint.getPoint()).isEqualTo(3000);
	}

	@Test
	@DisplayName("실패 - 기존포인트보다 많은 포인트를 사용하면 예외를 던진다.")
	void morePointThanOrigin_throwException() {

	  // given
		Long originPoint = 1000L;
		Long usePoint = 2000L;

		Point point = Point.of(originPoint);

	  // when & then
		assertThatThrownBy(() -> point.use(usePoint))
			.isInstanceOf(NotEnoughPointException.class);
	}
	
	@Test
	@DisplayName("성공 - 기존포인트와 사용포인트가 같으면 0원이된다.")
	void same_originPointAndUsePoint_zero() {
		
	  // given
		Long originPoint = 1000L;
		Long usePoint = 1000L;

	  // when
		Point point = Point.of(originPoint);
		point.use(usePoint);

		// then
		assertThat(point.getPoint()).isEqualTo(0);
	}

	@Test
	@DisplayName("성공 - 포인트를 사용하면 기존포인트에서 차감된다")
	void usePoint_substractPoint_success() {

	  // given
		long originPoint = 1000L;
		long usePoint = 500L;

		Point point = Point.of(originPoint);

		// when
		point.use(usePoint);

	  // then
		assertThat(point.getPoint()).isEqualTo(originPoint - usePoint);
	}

}