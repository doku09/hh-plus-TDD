package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointServiceConcurrencyTest {

	@Autowired
	private PointService pointService;

	@Test
	@DisplayName("성공 - 동시에 포인트 충전 테스트")
	void concurrency_charge() throws InterruptedException {
		// given
		long userId = 1L;
		int threadCount = 10;
		long chargeAmount = 100L;

		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch startLatch = new CountDownLatch(1); // 동시에 시작
		CountDownLatch doneLatch = new CountDownLatch(threadCount); // 모두 끝날 때까지 대기

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					startLatch.await(); // 시작 신호 대기
					pointService.charge(userId, chargeAmount);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // 스레드 상태 복구
				} finally {
					doneLatch.countDown(); // 하나 완료
				}
			});
		}

		startLatch.countDown(); // 모든 스레드에 시작 신호 전파
		doneLatch.await(); // 모든 스레드 작업 완료될 때까지 대기

		executorService.shutdown();

		// then
		UserPoint result = pointService.getPointByUserId(userId);
		assertThat(result.point()).isEqualTo(chargeAmount * threadCount);
	}

	@Test
	@DisplayName("성공 - 10개의 스레드가 포인트 사용에 동시요청")
	void concurrency_usePoint() throws InterruptedException {

		// given
		long id = 2L;
		int threadCount = 10;
		long amount = 100L;

		pointService.charge(id, 1500); // 새 유저에게 1000원 충전

		ExecutorService executorService = Executors.newFixedThreadPool(threadCount); // 매번 스레드를 만들지 않고 스레드 풀에 스레드 10개 생성
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(threadCount);

		for (int i = 0; i < 10; i++) {
			executorService.submit(() -> {
				try {
					startLatch.await();
					pointService.usePoint(id, amount);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					doneLatch.countDown();
				}
			});
		}

		// when
		startLatch.countDown(); //시작
		doneLatch.await();
		executorService.shutdown();

		// then
		UserPoint result = pointService.getPointByUserId(id);
		assertThat(result.point()).isEqualTo(500);
	}

}
