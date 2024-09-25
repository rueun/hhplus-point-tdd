package io.hhplus.tdd.point.domain.model.entity;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    /**
     * 사용자 포인트를 생성한다.
     * @param id 사용자 ID
     * @return 새로 생성된 사용자 포인트
     */
    public static UserPoint empty(final long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }


    /**
     * 사용자 포인트를 충전한다.
     * @param amount 충전할 포인트
     * @return 충전된 사용자 포인트
     */
    public UserPoint charge(final long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        }

        if (amount % 1_000 != 0) {
            throw new IllegalArgumentException("충전할 포인트는 1,000원 단위로 가능합니다.");
        }

        if (amount < 10_000) {
            throw new IllegalArgumentException("최소 충전 금액은 10,000원입니다.");
        }

        if (this.point + amount > 1_000_000) {
            throw new IllegalArgumentException("최대 잔고를 초과할 수 없습니다.");
        }

        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    /**
     * 사용자 포인트를 사용한다.
     * @param amount 사용할 포인트
     * @return 사용된 사용자 포인트
     */
    public UserPoint use(final long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용할 포인트는 0보다 커야 합니다.");
        }

        if (amount % 1_000 != 0) {
            throw new IllegalArgumentException("사용할 포인트는 1,000원 단위로 가능합니다.");
        }

        if (this.point < amount) {
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }

        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }
}
