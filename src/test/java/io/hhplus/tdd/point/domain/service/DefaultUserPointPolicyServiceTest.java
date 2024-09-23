package io.hhplus.tdd.point.domain.service;

import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultUserPointPolicyServiceTest {

    private DefaultUserPointPolicyService userPointPolicyService;

    @BeforeEach
    public void setUp() {
        userPointPolicyService = new DefaultUserPointPolicyService();
    }

    @ParameterizedTest
    @ValueSource(longs = {10000L, 20000L})
    @DisplayName("사용자 포인트를 충전할 수 있다.")
    void testValidateCharge_Success(long amount) {
        // Given
        UserPoint userPoint = new UserPoint(1L, 980_000L, System.currentTimeMillis());

        // When & Then: 예외가 발생하지 않는지 검증
        assertDoesNotThrow(() -> userPointPolicyService.validateCharge(userPoint, amount));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    @DisplayName("0 이하의 포인트는 충전할 수 없다.")
    void testValidateCharge_Fail_AmountIsZeroOrNegative(long amount) {
        // Given
        UserPoint userPoint = new UserPoint(1L, 50000L, System.currentTimeMillis());

        // When & Then: 금액이 0이거나 음수일 때 예외가 발생하는지 검증
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPointPolicyService.validateCharge(userPoint, amount));
        assert exception.getMessage().contains("충전할 포인트는 0보다 커야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {10001L, 10009L})
    @DisplayName("충전할 포인트는 1,000원 단위로 가능하다.")
    void testValidateCharge_Fail_AmountNotMultipleOfUnit(long amount) {
        // Given
        UserPoint userPoint = new UserPoint(1L, 50000L, System.currentTimeMillis());

        // When & Then: 1,000원 단위가 아닐 때 예외 발생
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPointPolicyService.validateCharge(userPoint, amount));
        assert exception.getMessage().contains("충전할 포인트는 1,000원 단위로 가능합니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {1000L, 9000L})
    @DisplayName("10,000원 미만의 포인트는 충전할 수 없다.")
    void testValidateCharge_Fail_AmountLessThanMinAmount(long amount) {
        // Given
        UserPoint userPoint = new UserPoint(1L,50000L, System.currentTimeMillis());

        // When & Then: 최소 금액보다 적을 때 예외 발생
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPointPolicyService.validateCharge(userPoint, amount));
        assert exception.getMessage().contains("최소 충전 금액은 10,000원입니다.");
    }


    @ParameterizedTest
    @ValueSource(longs = {11000L, 20000L})
    @DisplayName("최대 잔고를 초과하는 포인트는 충전할 수 없다.")
    void testValidateCharge_Fail_ExceedMaxPoint(long amount) {
        // Given
        UserPoint userPoint = new UserPoint(1L, 990_000L, System.currentTimeMillis());

        // When & Then: 충전 후 잔고가 최대치를 초과할 때 예외 발생
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPointPolicyService.validateCharge(userPoint, amount));
        assert exception.getMessage().contains("최대 잔고를 초과할 수 없습니다.");
    }
}