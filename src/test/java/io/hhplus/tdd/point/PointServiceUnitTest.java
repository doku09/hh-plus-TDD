package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.ErrorCode;
import io.hhplus.tdd.common.exception.MaxPointException;
import io.hhplus.tdd.common.exception.NegativeChargeAmountException;
import io.hhplus.tdd.common.exception.NotEnoughPointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PointServiceUnitTest {

	private final long FIX_TIME = System.currentTimeMillis();

	@InjectMocks
	private PointService pointService;

	@Mock
	private PointRepository pointRepository;

	@Nested
	@DisplayName("포인트 조회 테스트")
	class SelectPointTests {
		@Test
		@DisplayName("실패 - 사용자 아이디가 음수인경우 예외를 던진다.")
		void getPoint_negative_userId() {
			// Q) 이 케이스는 컨트롤러의 영역인가요?

			// given
			long userId = -1L;

			// when && then
			assertThatThrownBy(() -> pointService.getPointByUserId(userId))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("잘못된 사용자 아이디 입니다.");
		}

		@Test
		@DisplayName("성공 - 포인트 조회")
		void get_point_findByUserId() {
			// given
			long userId = 1L;

			// when
			when(pointRepository.getPointByUserId(userId))
				.thenReturn(new UserPoint(1L, 1000, System.currentTimeMillis()));

			UserPoint findUser = pointService.getPointByUserId(userId);

			// then
			verify(pointRepository).getPointByUserId(userId);
			assertThat(findUser).isNotNull();
			assertThat(findUser.id()).isEqualTo(userId);
			assertThat(findUser.point()).isEqualTo(1000);
		}
	}
	/**
	 * 포인트를 충전하는 서비스 함수
	 * 행동분석
	 * 1. 사용자 아이디와 충전할 포인트 금액을 파라미터로 받는다.
	 * 2. 파라미터를 검증한다.
	 * 3. 충전내역을 저장한다.
	 * 4. 충전 후 UserPoint를 반환한다.
	 * TC
	 * 1. 성공
	 * 유저아이디,충전할 포인트 -> 충전 후 유저포인트 반환
	 * 2. 실패
	 * - 사용자 ID가 0이하이면 실패한다.
	 * - 넘겨받은 포인트가 음수이면 실패한다.
	 */

	@Nested
	@DisplayName("포인트 충전 테스트")
	class ChargePointTests {
		@Test
		@DisplayName("실패 - 사용자 ID가 음수면 예외를 던진다.")
		void getNegativeUserId() {

			// given
			long id = -1L;
			long amount = 1000;

			// when && then
			assertThatThrownBy(() -> pointService.charge(id, amount))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(ErrorCode.NEGATIVE_USER_ID.getMessage());
		}

		@Test
		@DisplayName("실패 - 충전하려는 포인트가 음수이면 실패한다.")
		void fail_charge_point_negative() {

			// given
			long id = 1L;
			long amount = -1000;

			// when && then
			assertThatThrownBy(() -> pointService.charge(id, amount))
				.isInstanceOf(NegativeChargeAmountException.class)
				.hasMessage("충전할 금액은 음수가 될 수 없습니다.");
		}

		@Test
		@DisplayName("실패 - 사용자의 포인트가 최대 누적금액이 넘으면 예외를 던진다.")
		void max_amount() {

			// given
			long id = 1L;
			long amount = 6_000_000;

			// when
			doReturn(new UserPoint(1L, 5_000_000, FIX_TIME))
				.when(pointRepository).getPointByUserId(id);

			// then
			assertThat(PointConstants.MAX_POINT).isEqualTo(10_000_000);
			assertThatThrownBy(() -> pointService.charge(id, amount))
				.isInstanceOf(MaxPointException.class);
			verify(pointRepository).getPointByUserId(id);
		}

		@Test
		@DisplayName("성공 - 기존 사용자가 없으면 사용자를 추가하고 포인트를 충전한다.")
		void not_origin_user() {

			// given
			long id = 1L;
			long amount = 1000;

			// DB에 회원이 없을떄 기본 User객체를 만든다.
			doReturn(UserPoint.empty(id))
				.when(pointRepository).getPointByUserId(id);

			// 포인트를 1000원 충전한다.
			doReturn(new UserPoint(id, 1000, FIX_TIME))
				.when(pointRepository).charge(id, amount);

			// when
			UserPoint chargedUser = pointService.charge(id, amount);

			// then
			assertThat(chargedUser).isNotNull();
			assertThat(chargedUser.id()).isEqualTo(id);
			assertThat(chargedUser.point()).isEqualTo(1000);
		}

		@Test
		@DisplayName("성공 - 기존 사용자가 있으면 기존 포인트에 파라미터로 받은 포인트를 더한다.")
		void charge_point() {

			// given
			long id = 1L;
			long amount = 1000;

			when(pointRepository.getPointByUserId(id))
				.thenReturn(new UserPoint(id, 1000, FIX_TIME));

			when(pointRepository.charge(eq(id), anyLong())).thenAnswer(invocation -> {
				// pointService.charge안에서 repository.charge가 동작하는 타이밍에 동작한다.
				Long userId = invocation.getArgument(0);
				Long newPoint = invocation.getArgument(1);
				return new UserPoint(userId, newPoint, FIX_TIME);
			});

			// when
			UserPoint user = pointService.charge(id, amount);

			// then
			assertThat(user).isNotNull();
			assertThat(user.id()).isEqualTo(id);
			assertThat(user.point()).isEqualTo(2000);
		}
	}

	/**
	 * 포인트를 사용하는 서비스 함수
	 * 행동분석
	 * 1. 사용자 아이디와 사용할 포인트 금액을 파라미터로 받는다.
	 * 2. 파라미터를 검증한다.
	 * 3. 사용내역을 저장한다.
	 * 4. 사용 후 UserPoint를 반환한다.
	 * TC
	 * 1. 성공
	 * 유저아이디,사용할 포인트 -> 사용 후 유저포인트 반환
	 *
	 * 2. 실패
	 * - 사용자 ID가 0이하이면 실패한다.
	 * - 넘겨받은 포인트가 음수이면 실패한다.
	 * - 잔여 포인트가 사용할 포인트보다 적으면 실패한다.
	 */
	@Nested
	@DisplayName("포인트 사용 테스트")
	class UsePointTests {

		@Test
		@DisplayName("실패 - 사용자 ID가 0이하면 실패한다")
		void negativeUserId_fail() {

			// given
			long id = -1L;
			long amount = 1000;

			// then
			assertThatThrownBy(() -> pointService.usePoint(id,amount))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(ErrorCode.NEGATIVE_USER_ID.getMessage());
		}

		@Test
		@DisplayName("실패 - 넘겨받은 포인트가 음수이면 실패한다.")
		void negativeAmount_fail() {

			// given
			long id = 1L;
			long amount = -1000;

			// then
			assertThatThrownBy(() -> pointService.usePoint(id,amount))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(ErrorCode.NEGATIVE_AMOUNT.getMessage());
		}

		@Test
		@DisplayName("실패 - 잔여 포인트가 사용할 포인트보다 적으면 실패한다.")
		void notEnoughPoint_fail() {

		  // given
			long id = 1L;
			long amount = 10000;

			//when
			doReturn(UserPoint.empty(id))
				.when(pointRepository).getPointByUserId(id);

			// then
			assertThatThrownBy(() -> pointService.usePoint(id, amount))
				.isInstanceOf(NotEnoughPointException.class);
		}

		@Test
		@DisplayName("성공 - 포인트 사용 성공")
		void enoughPoint_success() {

		  // given
			long id = 1L;
			long amount = 10000;

		  // when
			doReturn(new UserPoint(id,20000,FIX_TIME))
				.when(pointRepository).getPointByUserId(id);

//			doAnswer(invocation -> {
//				Long userId = invocation.getArgument(0);
//				Long newPoint = invocation.getArgument(1);
//
//				return new UserPoint(userId,newPoint,FIX_TIME);
//			}).when(pointRepository.usePoint(id,amount));

			when(pointRepository.usePoint(id,amount))
				.thenAnswer(invocation -> {
					Long userId = invocation.getArgument(0);
					Long newPoint = invocation.getArgument(1);

					return new UserPoint(userId,newPoint,FIX_TIME);
				});

			UserPoint findUser = pointService.usePoint(id, amount);

			// then
			assertThat(findUser).isNotNull();
		}
	}
	/**
	 * 포인트 내역을 조회하는 기능
	 * 행동분석
	 * 1.
	 * 2.
	 * 3.
	 * 4.
	 * TC
	 * 1. 성공
	 *
	 *
	 * 2. 실패
	 * -
	 * -
	 * -
	 */
	@Nested
	@DisplayName("포인트 사용 테스트")
	class SaveHistoryTests {

	}
}