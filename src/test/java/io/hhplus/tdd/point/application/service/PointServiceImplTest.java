package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import io.hhplus.tdd.point.application.dto.UseUserPointCommand;
import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.domain.model.vo.TransactionType;
import io.hhplus.tdd.point.domain.repository.PointHistoryRepository;
import io.hhplus.tdd.point.domain.repository.UserPointRepository;
import io.hhplus.tdd.point.domain.service.UserPointPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserPointPolicyService userPointPolicyService;

    @InjectMocks
    private PointServiceImpl pointService;


    @Test
    @DisplayName("사용자 포인트를 충전할 수 있다.")
    void charge_ShouldChargeUserPoint() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 20000L, System.currentTimeMillis());
        when(userPointRepository.findByUserId(1L)).thenReturn(userPoint);

        // When
        UserPoint chargedPoint = pointService.charge(ChargeUserPointCommand.of(1L, 10000L));

        // Then
        assertAll(
                () -> assertNotNull(chargedPoint),
                () -> assertEquals(1L, chargedPoint.id()),
                () -> assertEquals(30000L, chargedPoint.point()),
                () -> verify(userPointRepository).save(chargedPoint),
                () -> verify(pointHistoryRepository).insert(1L, 10000L, TransactionType.CHARGE, chargedPoint.updateMillis()),
                () -> verify(userPointPolicyService).validateCharge(userPoint, 10000L)
        );
    }


    @Test
    @DisplayName("유효하지 않은 금액으로 포인트 충전 시 예외가 발생한다.")
    void charge_ShouldThrowException_WhenInvalidAmount() {
        // Given
        long userId = 1L;
        long invalidAmount = -1000L;
        ChargeUserPointCommand command = ChargeUserPointCommand.of(userId, invalidAmount);
        UserPoint userPoint = new UserPoint(userId, 20000L, System.currentTimeMillis());

        when(userPointRepository.findByUserId(userId)).thenReturn(userPoint);
        doThrow(new IllegalArgumentException("최소 충전 금액은 10,000원입니다."))
                .when(userPointPolicyService).validateCharge(userPoint, invalidAmount);


        // When & Then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> pointService.charge(command));
        assertEquals("최소 충전 금액은 10,000원입니다.", exception.getMessage());
        verify(userPointRepository).findByUserId(userId);
        verify(userPointPolicyService).validateCharge(userPoint, invalidAmount);
        verifyNoMoreInteractions(userPointRepository, pointHistoryRepository);
    }

    @Test
    @DisplayName("사용자 포인트를 사용할 수 있다.")
    void use_ShouldUseUserPoint() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 20000L, System.currentTimeMillis());
        when(userPointRepository.findByUserId(1L)).thenReturn(userPoint);

        // When
        UserPoint usedPoint = pointService.use(UseUserPointCommand.of(1L, 10000L));

        // Then
        assertAll(
                () -> assertNotNull(usedPoint),
                () -> assertEquals(1L, usedPoint.id()),
                () -> assertEquals(10000L, usedPoint.point()),
                () -> verify(userPointRepository).save(usedPoint),
                () -> verify(pointHistoryRepository).insert(1L, 10000L, TransactionType.USE, usedPoint.updateMillis()),
                () -> verify(userPointPolicyService).validateUse(userPoint, 10000L)
        );
    }


    @Test
    @DisplayName("유효하지 않은 금액으로 포인트 사용 시 예외가 발생한다.")
    void use_ShouldThrowException_WhenInvalidAmount() {
        // Given
        long userId = 1L;
        long invalidAmount = -1000L;
        UseUserPointCommand command = UseUserPointCommand.of(userId, invalidAmount);
        UserPoint userPoint = new UserPoint(userId, 20000L, System.currentTimeMillis());

        when(userPointRepository.findByUserId(userId)).thenReturn(userPoint);
        doThrow(new IllegalArgumentException("사용할 포인트는 0보다 커야 합니다."))
                .when(userPointPolicyService).validateUse(userPoint, invalidAmount);

        // When & Then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> pointService.use(command));
        assertEquals("사용할 포인트는 0보다 커야 합니다.", exception.getMessage());
        verify(userPointRepository).findByUserId(userId);
        verify(userPointPolicyService).validateUse(userPoint, invalidAmount);
        verifyNoMoreInteractions(userPointRepository, pointHistoryRepository);
    }


    @Test
    @DisplayName("사용자 포인트를 조회할 수 있다.")
    void getUserPointByUserId() {
        // Given
        UserPoint userPoint = new UserPoint(1L, 20000L, System.currentTimeMillis());
        when(userPointRepository.findByUserId(1L)).thenReturn(userPoint);

        // When
        UserPoint result = pointService.getUserPointByUserId(1L);

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(20000L, result.point())
        );
    }

    @Test
    @DisplayName("사용자 포인트 히스토리를 조회할 수 있다.")
    void getHistoriesByUserId() {
        // Given
        long userId = 1L;
        final List<PointHistory> pointHistories = List.of(
                new PointHistory(1L, 1L, 100L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, 1L, 100L, TransactionType.USE, System.currentTimeMillis()),
                new PointHistory(3L, 1L, 100L, TransactionType.CHARGE, System.currentTimeMillis())
        );

        when(pointHistoryRepository.selectAllByUserId(userId)).thenReturn(pointHistories);

        // When
        final List<PointHistory> historiesByUserId = pointService.getHistoriesByUserId(userId);

        // Then
        assertNotNull(historiesByUserId);
        verify(pointHistoryRepository).selectAllByUserId(userId);
        assertEquals(pointHistories, historiesByUserId);
    }
}