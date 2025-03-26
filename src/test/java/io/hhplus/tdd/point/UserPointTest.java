package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointTest {

    private static final long ANY_ID = 1L;
    private static final long ANY_UPDATE_MILLIS = 1L;

    @Test
    void 유저_아이디가_0보다_작은_경우_유저_포인트_생성_시_파라미터_예외_발생() {

        //when, then
        assertThatThrownBy(() -> new UserPoint(-1L, 1000L, ANY_UPDATE_MILLIS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유저 아이디 값은 0 이상이어야 합니다.");

    }

    @Test
    void 유저_아이디가_0인_경우_유저_포인트_생성_시_정상적으로_유저_포인트_생성() {

        //when
        UserPoint userPoint = new UserPoint(0L, 1000L, ANY_UPDATE_MILLIS);

        //then
        assertThat(userPoint).isEqualTo(new UserPoint(0L, 1000L, ANY_UPDATE_MILLIS));
    }

    @Test
    void 유저_포인트_금액이_0보다_작은_경우_유저_포인트_생성_시_파라미터_예외_발생() {

        //when, then
        assertThatThrownBy(() -> new UserPoint(ANY_ID, -1L, ANY_UPDATE_MILLIS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유저 포인트 값은 0 이상이어야 합니다.");
    }

    @Test
    void 유저_포인트_금액이_0인_유저_포인트_생성_시_정상적으로_유저_포인트_생성() {

        //when
        UserPoint userPoint = new UserPoint(ANY_ID, 0L, ANY_UPDATE_MILLIS);

        //then
        assertThat(userPoint).isEqualTo(new UserPoint(ANY_ID, 0L, ANY_UPDATE_MILLIS));
    }

    @Test
    void 충전_금액이_0보다_작을_경우_유저_포인트_충전_시_파라미터_예외_발생() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

        //when, then
        assertThatThrownBy(() -> userPoint.charge(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 0보다 커야 합니다.");
    }

    @Test
    void 충전_금액이_0일_경우_유저_포인트_충전_시_파라미터_예외_발생() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

        //when, then
        assertThatThrownBy(() -> userPoint.charge(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 0보다 커야 합니다.");
    }

    @Test
    void 충전_금액이_0보다_클_경우_유저_포인트_충전_시_정상적으로_충전된_유저_포인트_반환() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

        //when
        UserPoint result = userPoint.charge(1000L);

        //then
        assertThat(result).isEqualTo(new UserPoint(ANY_ID, 2000L, result.updateMillis()));
    }

    @Test
    void 유저_포인트_잔고와_충전_금액의_합이_최대_한도보다_클_경우_유저_포인트_충전_시_파라미터_예외_발생() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 100000L, ANY_UPDATE_MILLIS);

        //when, then
        assertThatThrownBy(() -> userPoint.charge(100001L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최대 한도를 초과하여 충전하는 것은 불가능합니다.");
    }

    @Test
    void 유저_포인트_잔고와_충전_금액의_합이_최대_한도와_같은_경우_유저_포인트_충전_시_정상적으로_충전된_유저_포인트_반환() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 100000L, ANY_UPDATE_MILLIS);

        //when
        UserPoint result = userPoint.charge(100000L);

        //then
        assertThat(result).isEqualTo(new UserPoint(ANY_ID, 200000L, result.updateMillis()));
    }

    @Test
    void 사용_금액이_0보다_작을_경우_유저_포인트_사용_시_파라미터_예외_발생() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

        //when, then
        assertThatThrownBy(() -> userPoint.use(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 금액은 0보다 커야 합니다.");
    }

    @Test
    void 사용_금액이_0일_경우_유저_포인트_사용_시_파라미터_예외_발생() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

        //when, then
        assertThatThrownBy(() -> userPoint.use(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 금액은 0보다 커야 합니다.");
    }

    @Test
    void 사용_금액이_0보다_클_경우_유저_포인트_사용_시_정상적으로_사용된_유저_포인트_반환() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 2000L, ANY_UPDATE_MILLIS);

        //when
        UserPoint result = userPoint.use(1000L);

        //then
        assertThat(result).isEqualTo(new UserPoint(ANY_ID, 1000L, result.updateMillis()));
    }

    @Test
    void 유저_포인트_잔고에_사용_금액을_뺀_값이_0보다_작을_경우_유저_포인트_사용_시_파라미터_예외_발생() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

        //when, then
        assertThatThrownBy(() -> userPoint.use(1001L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 가능한 금액을 초과하였습니다.");
    }

    @Test
    void 유저_포인트_잔고에_사용_금액을_뺀_값이_0일_경우_유저_포인트_사용_시_정상적으로_사용된_유저_포인트_반환() {

        //given
        UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

        //when
        UserPoint result = userPoint.use(1000L);

        //then
        assertThat(result).isEqualTo(new UserPoint(ANY_ID, 0L, result.updateMillis()));
    }
}