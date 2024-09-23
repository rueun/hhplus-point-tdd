package io.hhplus.tdd.point.domain.model.entity;

import io.hhplus.tdd.point.domain.service.UserPointPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserPointTest {

    private UserPointPolicyService mockPolicyService;

    @BeforeEach
    void setUp() {
        mockPolicyService = mock(UserPointPolicyService.class);
    }

    @Test
    @DisplayName("사용자 포인트를 empty 메서드를 통해 생성할 수 있다.")
    void create_empty_user_point() {
        // When
        UserPoint userPoint = UserPoint.empty(1L);

        // Then
        assertEquals(1L, userPoint.id());
        assertEquals(0L, userPoint.point());
        assertTrue(userPoint.updateMillis() > 0L);
    }

    @Test
    @DisplayName("포인트가 정상적으로 충전된다.")
    void charge_success() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When
        UserPoint updatedUserPoint = userPoint.charge(mockPolicyService, 10000L);

        // Then
        assertEquals(15000L, updatedUserPoint.point());
        assertTrue(updatedUserPoint.updateMillis() > userPoint.updateMillis());
    }

    @Test
    @DisplayName("정책 검증이 호출된다.")
    void charge_policy_validation_is_called() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When
        userPoint.charge(mockPolicyService, 10000L);

        // Then
        verify(mockPolicyService, times(1)).validateCharge(eq(userPoint), eq(10000L));
    }

    @Test
    @DisplayName("충전 금액이 0일 경우 정책에 의해 예외가 발생한다.")
    void charge_zero_amount_throws_exception() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        doThrow(new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다."))
                .when(mockPolicyService).validateCharge(any(UserPoint.class), anyLong());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.charge(mockPolicyService, 0L));

        assertEquals("충전할 포인트는 0보다 커야 합니다.", exception.getMessage());
    }
}