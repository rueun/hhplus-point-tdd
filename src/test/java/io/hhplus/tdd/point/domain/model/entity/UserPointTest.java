package io.hhplus.tdd.point.domain.model.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    @Test
    void 사용자_포인트를_empty_메서드를_통해_생성할_수_있다 () {
        // When
        UserPoint userPoint = UserPoint.empty(1L);

        // Then
        assertEquals(1L, userPoint.id());
        assertEquals(0L, userPoint.point());
        assertTrue(userPoint.updateMillis() > 0L);
    }

    @Test
    void 포인트가_정상적으로_충전된다() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When
        UserPoint updatedUserPoint = userPoint.charge(10000L);

        // Then
        assertEquals(15000L, updatedUserPoint.point());
    }


    @ParameterizedTest
    @ValueSource(longs = {0L, -1000L})
    void 충전_금액이_0보다_작은_경우_예외가_발생한다(long amount) {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.charge(amount));

        assertEquals("충전할 포인트는 0보다 커야 합니다.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {100L, 1100L})
    void 충전_금액은_1000원_단위여야_한다(long amount) {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.charge(amount));

        assertEquals("충전할 포인트는 1,000원 단위로 가능합니다.", exception.getMessage());
    }

    @Test
    void 최소_충전_금액은_10000원_이다() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.charge(9000L));

        assertEquals("최소 충전 금액은 10,000원입니다.", exception.getMessage());
    }

    @Test
    void 최대_잔고를_초과하는_포인트는_충전할_수_없다() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 980000L, System.currentTimeMillis());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.charge(21000L));

        assertEquals("최대 잔고를 초과할 수 없습니다.", exception.getMessage());
    }

    @Test
    void 사용자_포인트를_사용할_수_있다() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When
        UserPoint updatedUserPoint = userPoint.use(5000L);

        // Then
        assertEquals(0L, updatedUserPoint.point());
    }


    @ParameterizedTest()
    @ValueSource(longs = {0L, -1000L})
    void 사용_포인트는_0보다_커야_한다(long amount) {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.use(amount));

        assertEquals("사용할 포인트는 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    void 포인트는_1000원_단위로만_사용_가능하다() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.use(1001L));

        assertEquals("사용할 포인트는 1,000원 단위로 가능합니다.", exception.getMessage());
    }

    @Test
    void 잔고가_부족한_경우_포인트를_사용할_수_없다() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 5000L, System.currentTimeMillis());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPoint.use(6000L));

        assertEquals("잔고가 부족합니다.", exception.getMessage());
    }
}