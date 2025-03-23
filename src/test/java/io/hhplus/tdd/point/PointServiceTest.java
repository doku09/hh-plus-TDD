package io.hhplus.tdd.point;

import io.hhplus.tdd.common.exception.NotExistUserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PointServiceTest {

	@InjectMocks
	private PointService pointService;

	@Mock
	private PointRepository pointRepository;


	@Test
	@DisplayName("포인트를 조회하려는 사용자 정보가 없는 경우 예외를 던진다.")
	void getPoint_not_exist_user() {
		// given
		long userId = 1L; // 회원가입이 안되었거나 삭제된 사용자정보가 없는 사용자 id

	  // when && then
		assertThatThrownBy(() -> pointService.getPointByUserId(userId))
			.isInstanceOf(NotExistUserException.class)
			.hasMessage("요청한 사용자 정보를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("사용자 아이디가 음수인경우 예외를 던진다.")
	void getPoint_negative_userId() {
		// 이 케이스는 컨트롤러의 영역인가?

		// given
		long userId = -1L; // 회원가입이 안되었거나 삭제된 사용자정보가 없는 사용자 id

		// when && then
		assertThatThrownBy(() -> pointService.getPointByUserId(userId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("잘못된 사용자 아이디 입니다.");
	}

	@Test
	@DisplayName("사용자 아이디로 사용자의 포인트를 조회한다 - 성공")
	void getPoint_findByUserId() {
		// given
		long userId = 1L;

		// when
		when(pointRepository.getPointByUserId(userId)).thenReturn(new UserPoint(1L,1000,System.currentTimeMillis()));

		UserPoint findUser = pointService.getPointByUserId(userId);

		//then
		verify(pointRepository).getPointByUserId(userId);
		assertThat(findUser).isNotNull();
		assertThat(findUser.id()).isEqualTo(userId);
		assertThat(findUser.point()).isEqualTo(1000);
	}


}