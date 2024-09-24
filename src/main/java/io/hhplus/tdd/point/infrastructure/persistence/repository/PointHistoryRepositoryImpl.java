package io.hhplus.tdd.point.infrastructure.persistence.repository;

import io.hhplus.tdd.point.infrastructure.persistence.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.vo.TransactionType;
import io.hhplus.tdd.point.domain.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {
    private final PointHistoryTable pointHistoryTable;

    @Override
    public PointHistory insert(final long userId, final long amount, final TransactionType type, final long updateMillis) {
        return pointHistoryTable.insert(userId, amount, type, updateMillis);
    }

    @Override
    public List<PointHistory> selectAllByUserId(final long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
