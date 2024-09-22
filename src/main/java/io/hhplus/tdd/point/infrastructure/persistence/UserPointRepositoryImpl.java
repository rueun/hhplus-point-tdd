package io.hhplus.tdd.point.infrastructure.persistence;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.domain.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {
    private final UserPointTable userPointTable;

    /**
     * 사용자 포인트를 저장합니다.
     * @param userPoint 사용자 포인트 정보
     * @return 저장된 사용자 포인트 정보
     */
    @Override
    public UserPoint save(final UserPoint userPoint) {
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }


    /**
     * 사용자 ID로 사용자 포인트를 조회합니다.
     * 사용자 ID에 해당하는 사용자 포인트가 없을 경우 빈 UserPoint 객체를 반환합니다.
     * @param userId 사용자 ID
     * @return 사용자 포인트
     */
    @Override
    public UserPoint findByUserId(final long userId) {
        return userPointTable.selectById(userId);
    }
}
