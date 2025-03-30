package io.hhplus.tdd.point;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {

    public PointHistory {

        if (id < 0) {
            throw new IllegalArgumentException("포인트 내역 아이디 값은 0 이상이어야 합니다.");
        }
        if (userId < 0) {
            throw new IllegalArgumentException("유저 아이디 값은 0 이상이어야 합니다.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 혹은 사용 금액은 0보다 커야 합니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("트랜잭션 타입은 필수입니다.");
        }
    }
}
