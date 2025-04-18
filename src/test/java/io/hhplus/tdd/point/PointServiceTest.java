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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    UserPointTable userPointTable;
    @Mock
    PointHistoryTable pointHistoryTable;
    @InjectMocks
    PointService pointService;

    private static final long ANY_AMOUNT = 1000L;
    private static final TransactionType ANY_TRANSACTION_TYPE = TransactionType.CHARGE;
    private static final long ANY_UPDATE_MILLIS = 1L;

    @Nested
    class 유저_포인트_조회 {

        @Test
        void 특정_유저_아이디_값으로_포인트_조회_시_정상적으로_해당_유저_포인트_반환() {

            //given
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, ANY_AMOUNT, ANY_UPDATE_MILLIS));

            //when
            UserPoint result = pointService.getUserPointById(1L);

            //then
            assertThat(result).isEqualTo(new UserPoint(1L, ANY_AMOUNT, ANY_UPDATE_MILLIS));
        }
    }

    @Nested
    class 포인트_내역_조회 {

        @Test
        void 특정_유저_아이디_값으로_포인트_충전_사용_내역_조회_시_정상적으로_포인트_충전_사용_내역_목록_반환() {

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
            assertThat(result).isEqualTo(List.of(
                    new PointHistory(1L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS),
                    new PointHistory(2L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS),
                    new PointHistory(3L, 1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS)
            ));
        }
    }

    @Nested
    class 포인트_충전 {

        @Test
        void 특정_유저_아이디에_특정_금액만큼_포인트_충전_시_해당_유저_포인트_잔고에_충전_금액_더해진_유저_포인트_반환() {

            //given
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, ANY_AMOUNT, ANY_UPDATE_MILLIS));

            //when
            UserPoint result = pointService.charge(1L, 1000L);

            //then
            assertThat(result).isEqualTo(new UserPoint(1L, ANY_AMOUNT + 1000L, result.updateMillis()));
        }

        @Test
        void 특정_유저_아이디에_특정_금액만큼_포인트_충전_시_정상적으로_유저_포인트_업데이트와_포인트_충전_내역_저장() {

            //given
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, ANY_AMOUNT, ANY_UPDATE_MILLIS));

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
        void 특정_유저_아이디에_특정_금액만큼_포인트_사용_시_해당_유저_포인트_잔고에_충전_금액_뺀_유저_포인트_반환() {

            //given
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, ANY_AMOUNT, ANY_UPDATE_MILLIS));

            //when
            UserPoint result = pointService.use(1L, 1000L);

            //then
            assertThat(result).isEqualTo(new UserPoint(1L, ANY_AMOUNT - 1000L, result.updateMillis()));
        }

        @Test
        void 특정_유저_아이디에_특정_금액만큼_포인트_사용_시_정상적으로_유저_포인트_업데이트와_포인트_충전_내역_저장() {

            //given
            when(userPointTable.selectById(1L))
                    .thenReturn(new UserPoint(1L, ANY_AMOUNT, ANY_UPDATE_MILLIS));

            //when
            UserPoint result = pointService.use(1L, 1000L);

            //then
            verify(userPointTable, times(1)).insertOrUpdate(1L, ANY_AMOUNT - 1000L);
            verify(pointHistoryTable, times(1)).insert(1L, 1000L, TransactionType.USE, result.updateMillis());
        }
    }
}