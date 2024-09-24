package io.hhplus.tdd.point.domain.model.entity;

import io.hhplus.tdd.point.domain.service.UserPointPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    }

    @Test
    @DisplayName("포인트 충전 시 정책 검증이 호출된다.")
    void charge_policy_validation_is_called() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When
        userPoint.charge(mockPolicyService, 10000L);

        // Then
        verify(mockPolicyService, times(1)).validateCharge(any(UserPoint.class), anyLong());
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

    @Test
    @DisplayName("충전 금액이 1,000원 단위가 아닐 경우 정책에 의해 예외가 발생한다.")
    void charge_non_thousand_amount_throws_exception() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        doThrow(new IllegalArgumentException("충전할 포인트는 1,000원 단위로 가능합니다."))
                .when(mockPolicyService).validateCharge(any(UserPoint.class), anyLong());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.charge(mockPolicyService, 1001L));

        assertEquals("충전할 포인트는 1,000원 단위로 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("최소 충전 금액보다 작을 경우 정책에 의해 예외가 발생한다.")
    void charge_less_than_min_amount_throws_exception() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        doThrow(new IllegalArgumentException("최소 충전 금액은 10,000원입니다."))
                .when(mockPolicyService).validateCharge(any(UserPoint.class), anyLong());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.charge(mockPolicyService, 9000L));

        assertEquals("최소 충전 금액은 10,000원입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("최대 잔고를 초과할 경우 정책에 의해 예외가 발생한다.")
    void charge_more_than_max_point_throws_exception() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 980000L, System.currentTimeMillis());

        doThrow(new IllegalArgumentException("최대 잔고를 초과할 수 없습니다."))
                .when(mockPolicyService).validateCharge(any(UserPoint.class), anyLong());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.charge(mockPolicyService, 21000L));

        assertEquals("최대 잔고를 초과할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("포인트가 정상적으로 사용된다.")
    void use_success() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When
        UserPoint updatedUserPoint = userPoint.use(mockPolicyService, 3000L);

        // Then
        assertEquals(2000L, updatedUserPoint.point());
    }

    @Test
    @DisplayName("포인트 사용 시, 정책 검증이 호출된다.")
    void use_policy_validation_is_called() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When
        userPoint.use(mockPolicyService, 3000L);

        // Then
        verify(mockPolicyService, times(1)).validateUse(any(UserPoint.class), anyLong());
    }

    @Test
    @DisplayName("사용 금액이 0일 경우 정책에 의해 예외가 발생한다.")
void use_zero_amount_throws_exception() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        doThrow(new IllegalArgumentException("사용할 포인트는 0보다 커야 합니다."))
                .when(mockPolicyService).validateUse(any(UserPoint.class), anyLong());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.use(mockPolicyService, 0L));

        assertEquals("사용할 포인트는 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("사용 금액이 1,000원 단위가 아닐 경우 정책에 의해 예외가 발생한다.")
    void use_non_thousand_amount_throws_exception() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        doThrow(new IllegalArgumentException("사용할 포인트는 1,000원 단위로 가능합니다."))
                .when(mockPolicyService).validateUse(any(UserPoint.class), anyLong());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.use(mockPolicyService, 1001L));

        assertEquals("사용할 포인트는 1,000원 단위로 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("잔고가 부족할 경우 정책에 의해 예외가 발생한다.")
    void use_lack_of_balance_throws_exception() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        doThrow(new IllegalArgumentException("잔고가 부족합니다."))
                .when(mockPolicyService).validateUse(any(UserPoint.class), anyLong());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.use(mockPolicyService, 6000L));

        assertEquals("잔고가 부족합니다.", exception.getMessage());
    }
}