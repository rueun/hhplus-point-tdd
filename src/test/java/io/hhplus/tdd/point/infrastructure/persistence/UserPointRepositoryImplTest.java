package io.hhplus.tdd.point.infrastructure.persistence;

import io.hhplus.tdd.point.infrastructure.persistence.database.UserPointTable;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.infrastructure.persistence.repository.UserPointRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

class UserPointRepositoryImplTest {

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private UserPointRepositoryImpl userPointRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자 포인트를 저장할 수 있다.")
    void save_success() {
        // given
        UserPoint userPoint = new UserPoint(1, 1000, System.currentTimeMillis());
        given(userPointTable.insertOrUpdate(anyLong(), anyLong()))
                .willReturn(userPoint);

        // when
        UserPoint savedUserPoint = userPointRepository.save(userPoint);

        // then
        assertEquals(userPoint, savedUserPoint);
    }

    @Test
    @DisplayName("사용자 포인트가 null 인 경우 포인트를 저장할 수 없다.")
    void fail_save_null() {
        // given
        UserPoint userPoint = null;

        // when & then
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userPointRepository.save(userPoint));
        assertEquals("userPoint is null", exception.getMessage());
    }

    @Test
    @DisplayName("사용자 ID로 사용자 포인트를 조회할 수 있다.")
    void findByUserId() {
        // given
        UserPoint userPoint = new UserPoint(1, 1000, System.currentTimeMillis());
        given(userPointTable.selectById(anyLong()))
                .willReturn(userPoint);

        // when
        UserPoint foundUserPoint = userPointRepository.findByUserId(1);

        // then
        assertEquals(userPoint, foundUserPoint);
    }

    @Test
    @DisplayName("사용자 ID가 없는 경우 빈 UserPoint 객체를 반환한다.")
    void findByUserId_not_found() {
        // given
        given(userPointTable.selectById(anyLong()))
                .willReturn(UserPoint.empty(1));

        // when
        UserPoint foundUserPoint = userPointRepository.findByUserId(1);

        // then
        assertEquals(UserPoint.empty(1), foundUserPoint);
    }
}