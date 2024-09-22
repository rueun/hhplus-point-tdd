package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.domain.model.vo.TransactionType;
import io.hhplus.tdd.point.domain.repository.PointHistoryRepository;
import io.hhplus.tdd.point.domain.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    @Test
    @DisplayName("사용자 포인트를 충전할 수 있다.")
    void charge_shouldChargeUserPointSuccessfully() {
        // given
        when(userPointRepository.findByUserId(anyLong()))
                .thenReturn(new UserPoint(1L, 500L, System.currentTimeMillis()));

        // when
        UserPoint result = pointService.charge(ChargeUserPointCommand.of(1L, 100L));

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(600L, result.point()),
                () -> verify(userPointRepository).findByUserId(1L),
                () -> verify(userPointRepository).save(result),
                () -> verify(pointHistoryRepository).insert(eq(1L), eq(100L), eq(TransactionType.CHARGE), anyLong())
        );
    }


    @ParameterizedTest
    @ValueSource(longs = {-100L, 0L})
    @DisplayName("양수가 아닌 포인트는 충전할 수 없다.")
    void charge_shouldNotChargeNegativePoint(long amount) {
        // given
        when(userPointRepository.findByUserId(anyLong()))
                .thenReturn(new UserPoint(1L, 500L, System.currentTimeMillis()));

        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pointService.charge(ChargeUserPointCommand.of(1L, amount)));
        assertEquals("충전할 포인트는 0보다 커야 합니다.", exception.getMessage());
        verify(userPointRepository, never()).save(any());
        verify(pointHistoryRepository, never()).insert(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }
}