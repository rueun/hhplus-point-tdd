package io.hhplus.tdd.point.infrastructure.persistence;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.vo.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static io.hhplus.tdd.point.domain.model.vo.TransactionType.CHARGE;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class PointHistoryRepositoryImplTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointHistoryRepositoryImpl pointHistoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("포인트 히스토리를 저장할 수 있다.")
    void insert() {
        // given
        long currentTime = currentTimeMillis();
        final PointHistory pointHistory = new PointHistory(1L, 1L, 100L, CHARGE, currentTime);
        given(pointHistoryTable.insert(anyLong(), anyLong(), any(TransactionType.class), anyLong()))
                .willReturn(pointHistory);

        // when
        PointHistory savedPointHistory = pointHistoryRepository.insert(1L, 100L, CHARGE, currentTime);

        // then
        assertNotNull(savedPointHistory);
        assertEquals(pointHistory, savedPointHistory);
    }


    @Test
    @DisplayName("사용자 ID로 포인트 히스토리를 조회할 수 있다.")
    void selectAllByUserId() {
        // given
        final PointHistory pointHistory = new PointHistory(1L, 1L, 100L, CHARGE, currentTimeMillis());
        final List<PointHistory> pointHistories = List.of(pointHistory);
        given(pointHistoryTable.selectAllByUserId(anyLong()))
                .willReturn(pointHistories);

        // when
        List<PointHistory> resultPointHistories = pointHistoryRepository.selectAllByUserId(1L);

        // then
        assertEquals(pointHistories, resultPointHistories);
    }

}