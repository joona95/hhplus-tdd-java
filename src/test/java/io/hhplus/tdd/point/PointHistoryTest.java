package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PointHistoryTest {

    private static final long ANY_ID = 1L;
    private static final long ANY_USER_ID = 1L;
    private static final long ANY_AMOUNT = 1000L;
    private static final TransactionType ANY_TRANSACTION_TYPE = TransactionType.CHARGE;
    private static final long ANY_UPDATE_MILLIS = 1L;

    @Test
    void 포인트_내역_아이디_값이_0보다_작은_경우_포인트_내역_생성_시_파라미터_예외_발생() {

        //when, then
        assertThatThrownBy(() -> new PointHistory(-1L, ANY_USER_ID, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("포인트 내역 아이디 값은 0 이상이어야 합니다.");
    }

    @Test
    void 포인트_내역_아이디_값이_0인_경우_포인트_내역_생성_시_정상적으로_포인트_내역_생성() {

        //when
        PointHistory pointHistory = new PointHistory(0L, ANY_USER_ID, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

        //then
        assertThat(pointHistory).isEqualTo(new PointHistory(0L, ANY_USER_ID, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS));
    }

    @Test
    void 유저_아이디_값이_0보다_작은_경우_포인트_내역_생성_시_파라미터_예외_발생() {

        //when, then
        assertThatThrownBy(() -> new PointHistory(ANY_ID, -1L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유저 아이디 값은 0 이상이어야 합니다.");
    }

    @Test
    void 유저_아이디_값이_0인_경우_포인트_내역_생성_시_정상적으로_포인트_내역_생성() {

        //when
        PointHistory pointHistory = new PointHistory(ANY_ID, 0L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

        //then
        assertThat(pointHistory).isEqualTo(new PointHistory(ANY_ID, 0L, ANY_AMOUNT, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS));
    }

    @Test
    void 충전_혹은_사용_금액이_0보다_작은_경우_포인트_내역_생성_시_파라미터_예외_발생() {

        //when, then
        assertThatThrownBy(() -> new PointHistory(ANY_ID, ANY_USER_ID, -1L, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 혹은 사용 금액은 0보다 커야 합니다.");
    }

    @Test
    void 충전_혹은_사용_금액이_0인_경우_포인트_내역_생성_시_파라미터_예외_발생() {

        //when, then
        assertThatThrownBy(() -> new PointHistory(ANY_ID, ANY_USER_ID, 0L, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 혹은 사용 금액은 0보다 커야 합니다.");
    }

    @Test
    void 충전_혹은_사용_금액이_0보다_큰_경우_포인트_내역_생성_시_정상적으로_포인트_내역_생성() {

        //when
        PointHistory pointHistory = new PointHistory(ANY_ID, ANY_USER_ID, 1L, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS);

        //then
        assertThat(pointHistory).isEqualTo(new PointHistory(ANY_ID, ANY_USER_ID, 1L, ANY_TRANSACTION_TYPE, ANY_UPDATE_MILLIS));
    }

    @Test
    void 트랜잭션_타입_값이_없는_경우_포인트_내역_생성_시_파라미터_예외_발생() {

        //when, then
        assertThatThrownBy(() -> new PointHistory(ANY_ID, ANY_USER_ID, ANY_AMOUNT, null, ANY_UPDATE_MILLIS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("트랜잭션 타입은 필수입니다.");
    }
}