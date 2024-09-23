package io.hhplus.tdd.point.domain.service;

import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserPointPolicyService implements UserPointPolicyService {
    private static final long MIN_AMOUNT = 10_000L;
    private static final long MAX_POINT = 1_000_000L;
    private static final long AMOUNT_UNIT = 1_000L;

    /**
     * 충전할 포인트를 검증한다.
     * @param userPoint 사용자 포인트
     * @param amount 충전할 포인트
     * @throws IllegalArgumentException 충전할 포인트가 0보다 작거나 최소 충전 포인트보다 작은 경우
     * @throws IllegalArgumentException 최대 잔고를 초과하는 경우
     */
    @Override
    public void validateCharge(final UserPoint userPoint, final long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        }

        if (amount % AMOUNT_UNIT != 0) {
            throw new IllegalArgumentException("충전할 포인트는 1,000원 단위로 가능합니다.");
        }

        if (amount < MIN_AMOUNT) {
            throw new IllegalArgumentException("최소 충전 금액은 10,000원입니다.");
        }

        if (userPoint.point() + amount > MAX_POINT) {
            throw new IllegalArgumentException("최대 잔고를 초과할 수 없습니다.");
        }
    }
}
