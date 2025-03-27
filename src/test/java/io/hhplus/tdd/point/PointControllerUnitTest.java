package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerUnitTest {

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

	/**
	 * 포인트사용 컨트롤러에서는 서비스 로직만 있기에 요청 응답 테스트만 진행합니다.
	 */
	@Test
	@DisplayName("성공 - 포인트 사용")
	void chargePoint() throws Exception {

	  // given
		long id = 1L;
		long amount = 1000;

		HashMap<String, Long> map = new HashMap<>();
		map.put("amount",1000L);

		// when
		doReturn(new UserPoint(id, 1000, System.currentTimeMillis()))
			.when(pointService)
			.charge(id, amount);

	  // then
		mockMvc.perform(patch("/point/" + id + "/charge")
			.contentType(MediaType.TEXT_PLAIN)
			.content("1000"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(id))
			.andExpect(jsonPath("$.point").value(1000));
	}
	
	@Test
	@DisplayName("성공 - 사용자 아이디와 포인트를 넘기면 사용자에게 포인트를 충전한다.")
	void chargePoint_success() throws Exception {
		
	  // given
		long id = 1L;
		String amount = "1000";

	  // when
		when(pointService.charge(id,Long.parseLong(amount)))
			.thenReturn(UserPoint.empty(id));

	  // then
		mockMvc.perform(patch("/point/"+id+"/charge")
			.contentType(MediaType.TEXT_PLAIN)
			.content(amount)
		).andExpect(status().isOk());
	}

	@Test
	@DisplayName("성공 - 사용자 아이디를 넘기면 포인트 충전/사용 내역을 조회한다.")
	void getPOintHistory_success() throws Exception {

		// given
		long id = 1L;

		// when
		when(pointService.getHistoryByUserId(id))
			.thenReturn(anyList());

		// then
		mockMvc.perform(get("/point/"+id+"/histories"))
			.andExpect(status().isOk());
	}
}