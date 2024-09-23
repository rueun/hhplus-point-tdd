package io.hhplus.tdd.point.domain.model.entity;

import io.hhplus.tdd.point.domain.service.UserPointPolicyService;

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
     * @param pointPolicyService 포인트 정책 도메인 서비스
     * @param amount 충전할 포인트
     * @return 충전된 사용자 포인트
     */
    public UserPoint charge(final UserPointPolicyService pointPolicyService, final long amount) {
        pointPolicyService.validateCharge(this, amount);
        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    /**
     * 사용자 포인트를 사용한다.
     * @param pointPolicyService 포인트 정책 도메인 서비스
     * @param amount 사용할 포인트
     * @return 사용된 사용자 포인트
     */
    public UserPoint use(final UserPointPolicyService pointPolicyService, final long amount) {
        pointPolicyService.validateUse(this, amount);
        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }
}
