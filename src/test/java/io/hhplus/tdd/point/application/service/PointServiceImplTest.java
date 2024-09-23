package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
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
}