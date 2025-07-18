package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PointServiceIntegrationTest {

    UserPointTable userPointTable;
    PointHistoryTable pointHistoryTable;
    UserLockManager userLockManager;
    PointService pointService;

    @BeforeEach
    void before() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        userLockManager = new UserLockManager();
        pointService = new PointService(userPointTable, pointHistoryTable, userLockManager);
    }

    @Nested
    class 유저_포인트_조회 {

        @Test
        void 특정_유저의_유저_포인트가_존재할_때_유저_포인트_조회_시_해당_유저의_유저_포인트_반환() {

            //given
            userPointTable.insertOrUpdate(1L, 1000L);

            //when
            UserPoint result = pointService.getUserPointById(1L);

            //then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.point()).isEqualTo(1000L);
        }

        @Test
        void 특정_유저의_유저_포인트가_존재하지_않을_때_유저_포인트_조회_시_포인트_잔고_0을_가진_빈_유저_포인트_반환() {

            //when
            UserPoint result = pointService.getUserPointById(1L);

            //then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.point()).isEqualTo(0L);
        }

    }

    @Nested
    class 포인트_내역_조회 {

        @Test
        void 특정_유저의_포인트_내역이_존재할_때_포인트_내역_조회_시_해당_유저의_포인트_내역_목록_반환() {

            //given
            pointHistoryTable.insert(1L, 1000L, TransactionType.CHARGE, 1L);
            pointHistoryTable.insert(1L, 1000L, TransactionType.CHARGE, 1L);
            pointHistoryTable.insert(1L, 1000L, TransactionType.USE, 1L);
            pointHistoryTable.insert(2L, 1000L, TransactionType.CHARGE, 1L);

            //when
            List<PointHistory> result = pointService.getPointHistoriesByUserId(1L);

            //then
            assertThat(result)
                    .hasSize(3)
                    .isEqualTo(List.of(
                            new PointHistory(1L, 1L, 1000L, TransactionType.CHARGE, 1L),
                            new PointHistory(2L, 1L, 1000L, TransactionType.CHARGE, 1L),
                            new PointHistory(3L, 1L, 1000L, TransactionType.USE, 1L)
                    ));
        }

        @Test
        void 특정_유저의_포인트_내역이_존재하지_않을_때_포인트_내역_조회_시_빈_목록_반환() {

            //when
            List<PointHistory> result = pointService.getPointHistoriesByUserId(1L);

            //then
            assertThat(result)
                    .hasSize(0)
                    .isEqualTo(List.of());
        }
    }

    @Nested
    class 포인트_충전 {

        @Test
        void 특정_유저의_유저_포인트가_존재할_때_포인트_충전_시_해당_유저의_잔고에_충전_금액을_더한_유저_포인트_반환() {

            //given
            userPointTable.insertOrUpdate(1L, 1000L);

            //when
            UserPoint result = pointService.charge(1L, 1000L);

            //then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.point()).isEqualTo(2000L);
        }

        @Test
        void 특정_유저의_유저_포인트와_포인트_내역이_존재할_때_포인트_충전_시_충전된_유저_포인트와_함꼐_포인트_충전_내역_정상적으로_저장() {

            //given
            UserPoint userPoint = userPointTable.insertOrUpdate(1L, 1000L);
            PointHistory pointHistory = pointHistoryTable.insert(1L, 1000L, TransactionType.CHARGE, userPoint.updateMillis());

            //when
            UserPoint result = pointService.charge(1L, 1000L);

            //then
            assertThat(userPointTable.selectById(1L))
                    .isEqualTo(new UserPoint(1L, 2000L, result.updateMillis()));
            assertThat(pointHistoryTable.selectAllByUserId(1L))
                    .hasSize(2)
                    .isEqualTo(List.of(
                            pointHistory,
                            new PointHistory(2L, 1L, 1000L, TransactionType.CHARGE, result.updateMillis())
                    ));
        }
    }

    @Nested
    class 포인트_사용 {

        @Test
        void 특정_유저의_유저_포인트가_존재할_때_포인트_사용_시_해당_유저의_잔고에_사용_금액을_뺀_유저_포인트_반환() {

            //given
            userPointTable.insertOrUpdate(1L, 2000L);

            //when
            UserPoint result = pointService.use(1L, 1000L);

            //then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.point()).isEqualTo(1000L);
        }

        @Test
        void 특정_유저의_유저_포인트와_포인트_내역_존재할_때_포인트_사용_시_사용한_유저_포인트와_함꼐_포인트_사용_내역_정상적으로_저장() {

            //given
            UserPoint userPoint = userPointTable.insertOrUpdate(1L, 2000L);
            PointHistory pointHistory = pointHistoryTable.insert(1L, 2000L, TransactionType.CHARGE, userPoint.updateMillis());

            //when
            UserPoint result = pointService.use(1L, 1000L);

            //then
            assertThat(userPointTable.selectById(1L))
                    .isEqualTo(new UserPoint(1L, 1000L, result.updateMillis()));
            assertThat(pointHistoryTable.selectAllByUserId(1L))
                    .hasSize(2)
                    .isEqualTo(List.of(
                            pointHistory,
                            new PointHistory(2L, 1L, 1000L, TransactionType.USE, result.updateMillis())
                    ));
        }
    }

    @Nested
    class 동시성_테스트 {

        @Test
        void 특정_유저_충전_요청이_동시에_들어왔을_때_모든_요청들이_정상적으로_반영된_유저_포인트_값을_반환() throws InterruptedException {

            //given
            int threadCount = 20;

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

            CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            //when
            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    pointService.charge(1L, 1000L);
                    countDownLatch.countDown();
                });
            }

            countDownLatch.await();

            //then
            UserPoint userPoint = pointService.getUserPointById(1L);

            assertThat(userPoint.id()).isEqualTo(1L);
            assertThat(userPoint.point()).isEqualTo(1000L * threadCount);
        }

        @Test
        void 특정_유저_충전_요청이_동시에_들어왔을_때_모든_요청들이_정상적으로_반영된_유저_히스토리_내역을_저장() throws InterruptedException {

            //given
            int threadCount = 20;

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

            CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            //when
            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    pointService.charge(1L, 1000L);
                    countDownLatch.countDown();
                });
            }

            countDownLatch.await();

            //then
            assertThat(pointService.getPointHistoriesByUserId(1L)).hasSize(threadCount);
        }

        @Test
        void 특정_유저_사용_요청이_동시에_들어왔을_때_모든_요청들이_정상적으로_반영된_유저_포인트_값을_반환() throws InterruptedException {

            //given
            pointService.charge(1L, 20000L);

            int threadCount = 20;

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

            CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            //when
            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    pointService.use(1L, 1000L);
                    countDownLatch.countDown();
                });
            }

            countDownLatch.await();

            //then
            UserPoint userPoint = pointService.getUserPointById(1L);

            assertThat(userPoint.id()).isEqualTo(1L);
            assertThat(userPoint.point()).isEqualTo(0L);
        }

        @Test
        void 특정_유저_사용_요청이_동시에_들어왔을_때_모든_요청들이_정상적으로_반영된_유저_히스토리_내역을_저장() throws InterruptedException {

            //given
            pointService.charge(1L, 20000L);

            int threadCount = 20;

            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

            CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            //when
            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    pointService.use(1L, 1000L);
                    countDownLatch.countDown();
                });
            }

            countDownLatch.await();

            //then
            assertThat(pointService.getPointHistoriesByUserId(1L)).hasSize(threadCount + 1);
        }
    }
}