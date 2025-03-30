package io.hhplus.tdd.point;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPointTest {

    private static final long ANY_ID = 1L;
    private static final long ANY_UPDATE_MILLIS = 1L;

    @Nested
    class 유저_포인트_생성 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -10L, -3L, -2L, -1L})
        void 유저_아이디가_0보다_작으면_파라미터_예외_발생(long userId) {

            //when, then
            assertThatThrownBy(() -> new UserPoint(userId, 1000L, ANY_UPDATE_MILLIS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유저 아이디 값은 0 이상이어야 합니다.");

        }

        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 2L, 3L, 10L, 1000L})
        void 유저_아이디가_0이상이면_정상적으로_생성(long userId) {

            //when
            UserPoint userPoint = new UserPoint(userId, 1000L, ANY_UPDATE_MILLIS);

            //then
            assertThat(userPoint).isEqualTo(new UserPoint(userId, 1000L, ANY_UPDATE_MILLIS));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -10L, -3L, -2L, -1L})
        void 금액이_0보다_작으면_파라미터_예외_발생(long amount) {

            //when, then
            assertThatThrownBy(() -> new UserPoint(ANY_ID, amount, ANY_UPDATE_MILLIS))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유저 포인트 값은 0 이상이어야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 2L, 3L, 10L, 1000L})
        void 금액이_0이상이면_정상적으로_생성(long amount) {

            //when
            UserPoint userPoint = new UserPoint(ANY_ID, amount, ANY_UPDATE_MILLIS);

            //then
            assertThat(userPoint).isEqualTo(new UserPoint(ANY_ID, amount, ANY_UPDATE_MILLIS));
        }
    }

    @Nested
    class 유저_포인트_충전 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -10L, -3L, -2L, -1L, 0L})
        void 충전_금액이_0이하이면_파라미터_예외_발생(long amount) {

            //given
            UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

            //when, then
            assertThatThrownBy(() -> userPoint.charge(amount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("충전 금액은 0보다 커야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {100001L, 100002L, 100003L})
        void 유저_잔고와_충전_금액의_합이_최대_한도보다_클_경우_파라미터_예외_발생(long amount) {

            //given
            UserPoint userPoint = new UserPoint(ANY_ID, 100000L, ANY_UPDATE_MILLIS);

            //when, then
            assertThatThrownBy(() -> userPoint.charge(amount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 한도를 초과하여 충전하는 것은 불가능합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 2L, 3L, 10L, 1000L, 199000L})
        void 유저_포인트_잔고에_충전_금액이_더해져_유저_포인트_반환(long amount) {

            //given
            UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

            //when
            UserPoint result = userPoint.charge(amount);

            //then
            assertThat(result).isEqualTo(new UserPoint(ANY_ID, 1000L + amount, result.updateMillis()));
        }
    }

    @Nested
    class 유저_포인트_사용 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -10L, -3L, -2L, -1L, 0L})
        void 사용_금액이_0이하이면_작으면_파라미터_예외_발생(long amount) {

            //given
            UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

            //when, then
            assertThatThrownBy(() -> userPoint.use(amount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("사용 금액은 0보다 커야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {1001L, 1002L, 1003L, 2000L})
        void 유저_포인트_잔고에_사용_금액을_뺀_값이_0보다_작으면_파라미터_예외_발생(long amount) {

            //given
            UserPoint userPoint = new UserPoint(ANY_ID, 1000L, ANY_UPDATE_MILLIS);

            //when, then
            assertThatThrownBy(() -> userPoint.use(amount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("사용 가능한 금액을 초과하였습니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 2L, 3L, 10L, 1000L, 2000L})
        void 유저_포인트_작고에서_사용_금액을_뺀_유저_포인트_반환(long amount) {

            //given
            UserPoint userPoint = new UserPoint(ANY_ID, 2000L, ANY_UPDATE_MILLIS);

            //when
            UserPoint result = userPoint.use(amount);

            //then
            assertThat(result).isEqualTo(new UserPoint(ANY_ID, 2000L - amount, result.updateMillis()));
        }
    }
}