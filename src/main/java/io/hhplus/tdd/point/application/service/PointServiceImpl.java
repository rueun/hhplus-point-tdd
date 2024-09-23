package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.domain.model.vo.TransactionType;
import io.hhplus.tdd.point.domain.repository.PointHistoryRepository;
import io.hhplus.tdd.point.domain.repository.UserPointRepository;
import io.hhplus.tdd.point.domain.service.UserPointPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    private final UserPointPolicyService userPointPolicyService;

    @Override
    public UserPoint charge(final ChargeUserPointCommand command) {
        final UserPoint userPoint = userPointRepository.findByUserId(command.getUserId());
        final UserPoint chargedPoint = userPoint.charge(userPointPolicyService, command.getAmount());
        userPointRepository.save(chargedPoint);
        pointHistoryRepository.insert(command.getUserId(), command.getAmount(), TransactionType.CHARGE, chargedPoint.updateMillis());
        return chargedPoint;
    }
}
