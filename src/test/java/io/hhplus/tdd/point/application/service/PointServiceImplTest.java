package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import io.hhplus.tdd.point.application.dto.UseUserPointCommand;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.domain.model.vo.TransactionType;
import io.hhplus.tdd.point.domain.repository.PointHistoryRepository;
import io.hhplus.tdd.point.domain.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
    void id가_1인_사용자가_10000_포인트_충전_요청_시_충전_후_30000_포인트를_반환한다() {
        // Given
        UserPoint userPoint = mock(UserPoint.class);
        given(userPointRepository.findByUserId(anyLong())).willReturn(userPoint);
        given(userPoint.charge(anyLong())).willReturn(new UserPoint(1L, 30_000L, System.currentTimeMillis()));
        ChargeUserPointCommand command = ChargeUserPointCommand.of(1L, 10_000L);

        // When
        UserPoint chargedPoint = pointService.charge(command);

        // Then
        assertAll(
                () -> assertNotNull(chargedPoint),
                () -> assertEquals(1L, chargedPoint.id()),
                () -> assertEquals(30_000L, chargedPoint.point()),
                () -> then(userPointRepository).should().findByUserId(1L),
                () -> then(userPoint).should().charge(10_000L),
                () -> then(userPointRepository).should(times(1)).save(chargedPoint),
                () -> then(pointHistoryRepository).should(times(1)).insert(1L, 10_000L, TransactionType.CHARGE, chargedPoint.updateMillis())
        );
    }


    @Test
    void 유효하지_않은_금액으로_포인트_충전을_요청하는_경우_예외가_발생한다() {
        // Given
        UserPoint userPoint = mock(UserPoint.class);
        given(userPointRepository.findByUserId(anyLong())).willReturn(userPoint);
        doThrow(new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다."))
                .when(userPoint).charge(anyLong());
        ChargeUserPointCommand command = ChargeUserPointCommand.of(1L, -1000L);

        // When & Then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> pointService.charge(command));

        assertAll(
                () -> assertEquals("충전할 포인트는 0보다 커야 합니다.", exception.getMessage()),
                () -> then(userPointRepository).should().findByUserId(1L),
                () -> then(userPoint).should(times(1)).charge(-1000L),
                () -> then(userPointRepository).should(never()).save(any(UserPoint.class)),
                () -> then(pointHistoryRepository).should(never()).insert(anyLong(), anyLong(), any(TransactionType.class), anyLong())
        );
    }

    @Test
    void id가_1인_사용자가_10000_포인트_사용_요청_시_사용_후_20000_포인트를_반환한다() {
        // Given
        UserPoint userPoint = mock(UserPoint.class);
        given(userPointRepository.findByUserId(anyLong())).willReturn(userPoint);
        given(userPoint.use(anyLong())).willReturn(new UserPoint(1L, 20_000L, System.currentTimeMillis()));
        UseUserPointCommand command = UseUserPointCommand.of(1L, 10_000L);

        // When
        UserPoint usedPoint = pointService.use(command);

        // Then
        assertAll(
                () -> assertNotNull(usedPoint),
                () -> assertEquals(1L, usedPoint.id()),
                () -> assertEquals(20_000L, usedPoint.point()),
                () -> then(userPointRepository).should().findByUserId(1L),
                () -> then(userPoint).should().use(10_000L),
                () -> then(userPointRepository).should(times(1)).save(usedPoint),
                () -> then(pointHistoryRepository).should(times(1)).insert(1L, 10_000L, TransactionType.USE, usedPoint.updateMillis()));
    }


    @Test
    @DisplayName("유효하지 않은 금액으로 포인트 사용 시 예외가 발생한다.")
    void 유효하지_않은_금액으로_포인트를_사용하는_경우_예외가_발생한다() {
        // Given
        UserPoint userPoint = mock(UserPoint.class);
        given(userPointRepository.findByUserId(anyLong())).willReturn(userPoint);
        doThrow(new IllegalArgumentException("사용할 포인트는 0보다 커야 합니다."))
                .when(userPoint).use(anyLong());
        UseUserPointCommand command = UseUserPointCommand.of(1L, -1000L);

        // When & Then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> pointService.use(command));

        assertAll(
                () -> assertEquals("사용할 포인트는 0보다 커야 합니다.", exception.getMessage()),
                () -> then(userPointRepository).should().findByUserId(1L),
                () -> then(userPoint).should(times(1)).use(-1000L),
                () -> then(userPointRepository).should(never()).save(any(UserPoint.class)),
                () -> then(pointHistoryRepository).should(never()).insert(anyLong(), anyLong(), any(TransactionType.class), anyLong())
        );
    }
}