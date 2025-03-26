package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    private static final long MAX_POINT_LIMIT = 200000L;

    public UserPoint {

        if (id < 0) {
            throw new IllegalArgumentException("유저 아이디 값은 0 이상이어야 합니다.");
        }
        if (point < 0) {
            throw new IllegalArgumentException("유저 포인트 값은 0 이상이어야 합니다.");
        }
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint charge(long amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        if (MAX_POINT_LIMIT < point + amount) {
            throw new IllegalArgumentException("최대 한도를 초과하여 충전하는 것은 불가능합니다.");
        }

        return new UserPoint(id, point + amount, System.currentTimeMillis());
    }

    public UserPoint use(long amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (point - amount < 0) {
            throw new IllegalArgumentException("사용 가능한 금액을 초과하였습니다.");
        }

        return new UserPoint(id, point - amount, System.currentTimeMillis());
    }
}
