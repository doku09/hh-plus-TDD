package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PointService pointService;

	@Test
	@DisplayName("음수의 사용자 아이디를 요청하면 BAD REQUEST 예외를 던진다.")
	public void getPoint_IllegalException() throws Exception {
		// given
		long id = -1L;

		// when && then
		mockMvc.perform(get("/point/" + id))
			.andExpect(status().isBadRequest())
			.andExpect(result -> assertInstanceOf(IllegalArgumentException.class, result.getResolvedException()));
	}

	@Test
	@DisplayName("사용자 포인트 조회 - 성공")
	public void getUserPoint() throws Exception {
		// given
		long id = 1L;

		// when
		doReturn(new UserPoint(id, 1000, System.currentTimeMillis()))
			.when(pointService)
			.getPointByUserId(id);

		// then
		mockMvc.perform(get("/point/" + id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(id))
			.andExpect(jsonPath("$.point").value(1000));
	}
}