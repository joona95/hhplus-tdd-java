package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    UserPointTable userPointTable;
    @Mock
    PointHistoryTable pointHistoryTable;
    @Mock
    UserLockManager userLockManager;
    @InjectMocks
    PointService pointService;

    private static final long ANY_AMOUNT = 1000L;
    private static final TransactionType ANY_TRANSACTION_TYPE = TransactionType.CHARGE;
    private static final long ANY_UPDATE_MILLIS = 1L;

    @Nested
    class 유저_포인트_조회 {

        @Test
        void 특정_유저_포인트_정상적으로_반환() {

            //given
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, ANY_AMOUNT, ANY_UPDATE_MILLIS));

            //when
            UserPoint result = pointService.getUserPointById(1L);

            //then
            assertThat(result).isEqualTo(new UserPoint(1L, ANY_AMOUNT, ANY_UPDATE_MILLIS));
        }

        @Test
        void 특정_유저_포인트_조회_정상적으로_요청() {

            //when
            pointService.getUserPointById(1L);

            //then
            verify(userPointTable, times(1)).selectById(1L);
        }
    }

    @Nested
    class 포인트_내역_조회 {

        @Test
        void 특정_유저_포인트_내역_정상적으로_반환() {

            //given
            List<PointHistory> pointHistories = List.of(
                    new PointHistory(1L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS),
                    new PointHistory(2L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS),
                    new PointHistory(3L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS)
            );

            when(pointHistoryTable.selectAllByUserId(1L))
                    .thenReturn(pointHistories);

            //when
            List<PointHistory> result = pointService.getPointHistoriesByUserId(1L);

            //then
            assertThat(result).hasSize(3);
            assertThat(result).isEqualTo(pointHistories);
        }

        @Test
        void 특정_유저_포인트_내역_조회_정상적으로_요청() {

            //when
            pointService.getPointHistoriesByUserId(1L);

            //then
            verify(pointHistoryTable, times(1)).selectAllByUserId(1L);
        }
    }

    @Nested
    class 포인트_충전 {

        @Test
        void 유저_포인트_조회_실패_시_유저_포인트_저장과_포인트_내역_저장_요청이_발생하지_않음() {

            //given
            when(userLockManager.getLock(1L))
                    .thenReturn(new ReentrantLock());
            when(userPointTable.selectById(1L))
                    .thenThrow(new RuntimeException());

            //when, then
            assertThatThrownBy(() -> pointService.charge(1L, 1000L))
                    .isInstanceOf(RuntimeException.class);
            verify(userPointTable, times(0)).insertOrUpdate(1L, ANY_AMOUNT + 1000L);
            verify(pointHistoryTable, times(0)).insert(1L, 1000L, TransactionType.CHARGE, ANY_UPDATE_MILLIS);
        }

        @Test
        void 유저_포인트_저장_실패_시_포인트_내역_저장_요청이_발생하지_않음() {

            //given
            when(userLockManager.getLock(1L))
                    .thenReturn(new ReentrantLock());
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, 1000L, ANY_UPDATE_MILLIS));
            when(userPointTable.insertOrUpdate(1L, 2000L))
                    .thenThrow(new RuntimeException());

            //when, then
            assertThatThrownBy(() -> pointService.charge(1L, 1000L))
                    .isInstanceOf(RuntimeException.class);
            verify(pointHistoryTable, times(0)).insert(1L, 1000L, TransactionType.CHARGE, ANY_UPDATE_MILLIS);
        }

        @Test
        void 특정_유저_포인트_잔고에_충전_금액_더해진_유저_포인트_반환() {

            //given
            when(userLockManager.getLock(1L))
                    .thenReturn(new ReentrantLock());
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, 1000L, ANY_UPDATE_MILLIS));
            when(userPointTable.insertOrUpdate(1L, 2000L))
                    .thenReturn(new UserPoint(1L, 2000L, ANY_UPDATE_MILLIS));

            //when
            UserPoint result = pointService.charge(1L, 1000L);

            //then
            assertThat(result).isEqualTo(new UserPoint(1L, ANY_AMOUNT + 1000L, result.updateMillis()));
        }

        @Test
        void 유저_포인트_업데이트와_포인트_내역_저장_정상적으로_요청() {

            //given
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, 1000L, ANY_UPDATE_MILLIS));
            when(userPointTable.insertOrUpdate(1L, 2000L))
                    .thenReturn(new UserPoint(1L, 2000L, ANY_UPDATE_MILLIS));
            when(userLockManager.getLock(1L))
                    .thenReturn(new ReentrantLock());

            //when
            UserPoint result = pointService.charge(1L, 1000L);

            //then
            verify(userPointTable, times(1)).insertOrUpdate(1L, ANY_AMOUNT + 1000L);
            verify(pointHistoryTable, times(1)).insert(1L, 1000L, TransactionType.CHARGE, result.updateMillis());
        }
    }

    @Nested
    class 포인트_사용 {

        @Test
        void 유저_포인트_조회_실패_시_유저_포인트_저장과_포인트_내역_저장_요청이_발생하지_않음() {

            //given
            when(userLockManager.getLock(1L))
                    .thenReturn(new ReentrantLock());
            when(userPointTable.selectById(1L))
                    .thenThrow(new RuntimeException());

            //when, then
            assertThatThrownBy(() -> pointService.use(1L, 1000L))
                    .isInstanceOf(RuntimeException.class);
            verify(userPointTable, times(0)).insertOrUpdate(1L, ANY_AMOUNT - 1000L);
            verify(pointHistoryTable, times(0)).insert(1L, 1000L, TransactionType.USE, ANY_UPDATE_MILLIS);
        }

        @Test
        void 유저_포인트_저장_실패_시_포인트_내역_저장_요청이_발생하지_않음() {

            //given
            when(userLockManager.getLock(1L))
                    .thenReturn(new ReentrantLock());
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, 2000L, ANY_UPDATE_MILLIS));
            when(userPointTable.insertOrUpdate(1L, 1000L))
                    .thenThrow(new RuntimeException());

            //when, then
            assertThatThrownBy(() -> pointService.use(1L, 1000L))
                    .isInstanceOf(RuntimeException.class);
            verify(pointHistoryTable, times(0)).insert(1L, 1000L, TransactionType.CHARGE, ANY_UPDATE_MILLIS);
        }

        @Test
        void 특정_유저_포인트_잔고에_사용_금액_뺀_유저_포인트_반환() {

            //given
            when(userLockManager.getLock(1L))
                    .thenReturn(new ReentrantLock());
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, 2000L, ANY_UPDATE_MILLIS));
            when(userPointTable.insertOrUpdate(1L, 1000L))
                    .thenReturn(new UserPoint(1L, 1000L, ANY_UPDATE_MILLIS));

            //when
            UserPoint result = pointService.use(1L, 1000L);

            //then
            assertThat(result).isEqualTo(new UserPoint(1L, 1000L, result.updateMillis()));
        }

        @Test
        void 특정_유저_포인트_업데이트와_포인트_내역_저장_정상적으로_요청() {

            //given
            when(userLockManager.getLock(1L))
                    .thenReturn(new ReentrantLock());
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, 2000L, ANY_UPDATE_MILLIS));
            when(userPointTable.insertOrUpdate(1L, 1000L))
                    .thenReturn(new UserPoint(1L, 1000L, ANY_UPDATE_MILLIS));

            //when
            UserPoint result = pointService.use(1L, 1000L);

            //then
            verify(userPointTable, times(1)).insertOrUpdate(1L, 1000L);
            verify(pointHistoryTable, times(1)).insert(1L, 1000L, TransactionType.USE, result.updateMillis());
        }
    }
}