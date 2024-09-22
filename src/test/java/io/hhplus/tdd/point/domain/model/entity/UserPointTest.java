package io.hhplus.tdd.point.domain.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    @Test
    @DisplayName("사용자의 포인트를 충전할 수 있다.")
    void charge_success() {
        // given
        UserPoint userPoint = UserPoint.empty(1);
        // when
        UserPoint chargedUserPoint = userPoint.charge(100);
        // then
        assertEquals(100, chargedUserPoint.point());
    }

    @Test
    @DisplayName("0 미만의 포인트는 충전할 수 없다.")
    void charge_fail_negative_point() {
        // given
        UserPoint userPoint = UserPoint.empty(1);

        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPoint.charge(-100));
        assertEquals("충전할 포인트는 0 이상이어야 합니다.", exception.getMessage());
    }
}