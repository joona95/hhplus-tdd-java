package io.hhplus.tdd.point;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class PointHistoryTest {

    private static final long ANY_ID = 1L;
    private static final long ANY_USER_ID = 1L;
    private static final long ANY_AMOUNT = 1000L;
    private static final TransactionType ANY_TRANSACTION_TYPE = TransactionType.CHARGE;
    private static final long ANY_UPDATE_MILLIS = 1L;

    @Nested
    class 포인트_내역_생성 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -10L, -3L, -2L, -1L})
        void 아이디_값이_0보다_작으면_파라미터_예외_발생(long id) {

            //when, then
            assertThatThrownBy(() -> new PointHistory(id, ANY_USER_ID, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("포인트 내역 아이디 값은 0 이상이어야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 2L, 3L, 10L, 1000L})
        void 아이디_값이_0이상이면_정상적으로_생성(long id) {

            //when
            PointHistory pointHistory = new PointHistory(id, ANY_USER_ID, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

            //then
            assertThat(pointHistory).isEqualTo(new PointHistory(id, ANY_USER_ID, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -10L, -3L, -2L, -1L})
        void 유저_아이디_값이_0보다_작으면_파라미터_예외_발생(long userId) {

            //when, then
            assertThatThrownBy(() -> new PointHistory(ANY_ID, userId, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유저 아이디 값은 0 이상이어야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 2L, 3L, 10L, 1000L})
        void 유저_아이디_값이_0이상이면_정상적으로_생성(long userId) {

            //when
            PointHistory pointHistory = new PointHistory(ANY_ID, userId, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

            //then
            assertThat(pointHistory).isEqualTo(new PointHistory(ANY_ID, userId, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -10L, -3L, -2L, -1L, 0L})
        void 금액이_0이하이면_파라미터_예외_발생(long amount) {

            //when, then
            assertThatThrownBy(() -> new PointHistory(ANY_ID, ANY_USER_ID, amount, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("충전 혹은 사용 금액은 0보다 커야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 2L, 3L, 10L, 1000L})
        void 금액이_0초과이면_정상적으로_생성(long amount) {

            //when
            PointHistory pointHistory = new PointHistory(ANY_ID, ANY_USER_ID, amount, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

            //then
            assertThat(pointHistory).isEqualTo(new PointHistory(ANY_ID, ANY_USER_ID, amount, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS));
        }

        @ParameterizedTest
        @NullSource
        void 트랜잭션_타입이_null_이면_파라미터_예외_발생(TransactionType type) {

            //when, then
            assertThatThrownBy(() -> new PointHistory(ANY_ID, ANY_USER_ID, ANY_AMOUNT, type, ANY_UPDATE_MILLIS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("트랜잭션 타입은 필수입니다.");
        }
    }
}