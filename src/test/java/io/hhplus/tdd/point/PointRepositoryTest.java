package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PointRepositoryTest {

	private PointRepository pointRepository;
	private UserPointTable db;
	@BeforeEach
	void setUp() {
		db = new UserPointTable();
		pointRepository = new PointRepository(db);
	}

	@Test
	@DisplayName("id에 대한 유효성검사를 진행해야할지? 음수같은 값")
	void parameterValidation() {
	}
	
	@Test
	@DisplayName("사용자 정보가 없으면 기본값을 생성하여 반환한다.")
	void getDefaultUser() {
		
	  //given
		long id = 1L;
	  //when
		UserPoint user = pointRepository.getPointByUserId(id);

		//then
		assertNotNull(user);
		assertThat(user.id()).isEqualTo(1L);
		assertThat(user.point()).isEqualTo(0);
	}
}