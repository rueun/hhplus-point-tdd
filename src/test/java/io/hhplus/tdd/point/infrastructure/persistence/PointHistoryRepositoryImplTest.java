package io.hhplus.tdd.point.infrastructure.persistence;

import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.infrastructure.persistence.database.PointHistoryTable;
import io.hhplus.tdd.point.infrastructure.persistence.repository.PointHistoryRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.hhplus.tdd.point.domain.model.vo.TransactionType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointHistoryRepositoryImplTest {

    @Autowired
    private PointHistoryTable pointHistoryTable;

    private PointHistoryRepositoryImpl pointHistoryRepository;

    @BeforeEach
    void setUp() {
        pointHistoryRepository = new PointHistoryRepositoryImpl(pointHistoryTable);
    }

    @Test
    @DisplayName("포인트 히스토리를 저장할 수 있다.")
    void insert() {
        // given
        long currentTime = currentTimeMillis();

        // when
        PointHistory savedPointHistory = pointHistoryRepository.insert(1L, 100L, CHARGE, currentTime);

        // then
        assertAll(
                () -> assertNotNull(savedPointHistory),
                () -> assertEquals(1L, savedPointHistory.userId()),
                () -> assertEquals(100L, savedPointHistory.amount()),
                () -> assertEquals(CHARGE, savedPointHistory.type()),
                () -> assertEquals(currentTime, savedPointHistory.updateMillis())
        );
    }


    @Test
    @DisplayName("사용자 ID로 포인트 히스토리를 조회할 수 있다.")
    void selectAllByUserId() {
        // given
        long currentTime = currentTimeMillis();
        pointHistoryRepository.insert(1L, 100L, CHARGE, currentTime);
        pointHistoryRepository.insert(1L, 200L, CHARGE, currentTime);
        pointHistoryRepository.insert(1L, 300L, CHARGE, currentTime);

        // when
        List<PointHistory> resultPointHistories = pointHistoryRepository.selectAllByUserId(1L);

        // then
        assertAll(
                () -> assertNotNull(resultPointHistories),
                () -> assertEquals(3, resultPointHistories.size()),
                () -> assertThat(resultPointHistories).containsExactlyInAnyOrder(
                        new PointHistory(1L, 1L, 100L, CHARGE, currentTime),
                        new PointHistory(2L, 1L, 200L, CHARGE, currentTime),
                        new PointHistory(3L, 1L, 300L, CHARGE, currentTime)
                )
        );
    }

}