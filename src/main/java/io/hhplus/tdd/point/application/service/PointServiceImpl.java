package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import io.hhplus.tdd.point.application.dto.UseUserPointCommand;
import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.domain.model.vo.TransactionType;
import io.hhplus.tdd.point.domain.repository.PointHistoryRepository;
import io.hhplus.tdd.point.domain.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    public UserPoint charge(final ChargeUserPointCommand command) {
        final UserPoint userPoint = userPointRepository.findByUserId(command.getUserId());
        final UserPoint chargedPoint = userPoint.charge(command.getAmount());
        userPointRepository.save(chargedPoint);
        pointHistoryRepository.insert(command.getUserId(), command.getAmount(), TransactionType.CHARGE, chargedPoint.updateMillis());
        return chargedPoint;
    }

    @Override
    public UserPoint use(final UseUserPointCommand command) {
        final UserPoint userPoint = userPointRepository.findByUserId(command.getUserId());
        final UserPoint usedPoint = userPoint.use(command.getAmount());
        userPointRepository.save(usedPoint);
        pointHistoryRepository.insert(command.getUserId(), command.getAmount(), TransactionType.USE, usedPoint.updateMillis());
        return usedPoint;
    }

    @Override
    public UserPoint getUserPointByUserId(final long userId) {
        return userPointRepository.findByUserId(userId);
    }

    @Override
    public List<PointHistory> getHistoriesByUserId(final long userId) {
        return pointHistoryRepository.selectAllByUserId(userId);
    }
}
