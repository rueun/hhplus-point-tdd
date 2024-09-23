package io.hhplus.tdd.point.domain.model.entity;

import io.hhplus.tdd.point.domain.service.DefaultUserPointPolicyService;
import io.hhplus.tdd.point.domain.service.UserPointPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    private final UserPointPolicyService pointPolicyService = new DefaultUserPointPolicyService();

    @Test
    @DisplayName("사용자의 포인트를 충전할 수 있다.")
    void charge_success() {
        // given
        UserPoint userPoint = new UserPoint(1L, 100L, currentTimeMillis());
        // when
        UserPoint chargedUserPoint = userPoint.charge(pointPolicyService, 100L);
        // then
        assertEquals(200L, chargedUserPoint.point());
    }


    @ParameterizedTest
    @ValueSource(longs = {-100L, 0L})
    @DisplayName("0 미만의 포인트는 충전할 수 없다.")
    void charge_fail_negative_point(long amount) {
        // given
        UserPoint userPoint = new UserPoint(1L, 100L, currentTimeMillis());

        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPoint.charge(pointPolicyService, amount));
        assertEquals("충전할 포인트는 0보다 커야 합니다.", exception.getMessage());
    }
}