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
     * 포인트를 충전한다.
     *
     * @param amount 충전할 포인트 양
     * @return 충전 후 사용자 포인트
     */
    public UserPoint charge(final long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        }
        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }
}
