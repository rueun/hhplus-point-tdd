package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPointTableTest {

    private UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
    }

    @Test
    @DisplayName("사용자 포인트를 생성할 수 있다.")
    void insertOrUpdate() {
        // given
        long userId = 1L;
        long amount = 1000L;

        // when
        UserPoint savedUserPoint = userPointTable.insertOrUpdate(userId, amount);

        // then
        assertNotNull(savedUserPoint);
        assertEquals(userId, savedUserPoint.id());
        assertEquals(amount, savedUserPoint.point());
    }

    @Test
    @DisplayName("사용자 ID로 사용자 포인트를 조회할 수 있다.")
    void selectById() {
        // given
        long userId = 1L;
        long amount = 1000L;
        userPointTable.insertOrUpdate(userId, amount);

        // when
        UserPoint foundUserPoint = userPointTable.selectById(userId);

        // then
        assertNotNull(foundUserPoint);
        assertEquals(userId, foundUserPoint.id());
        assertEquals(amount, foundUserPoint.point());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 시 빈 UserPoint 객체를 반환한다.")
    void selectById_whenUserIdNotFound() {
        // given
        long userId = 999L; // 존재하지 않는 ID

        // when
        UserPoint foundUserPoint = userPointTable.selectById(userId);

        // then
        assertNotNull(foundUserPoint);
        assertEquals(userId, foundUserPoint.id());
        assertEquals(0, foundUserPoint.point());
    }

    @Test
    @DisplayName("insertOrUpdate 후 동일한 ID로 업데이트하면 포인트가 변경된다.")
    void insertOrUpdate_updatesExistingUserPoint() {
        // given
        long userId = 1L;
        userPointTable.insertOrUpdate(userId, 1000L);

        // when
        UserPoint updatedUserPoint = userPointTable.insertOrUpdate(userId, 2000L);

        // then
        assertEquals(userId, updatedUserPoint.id());
        assertEquals(2000L, updatedUserPoint.point());
    }
}