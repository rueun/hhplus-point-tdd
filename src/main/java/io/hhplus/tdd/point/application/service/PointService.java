package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import io.hhplus.tdd.point.application.dto.UseUserPointCommand;
import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;

import java.util.List;

public interface PointService {
    UserPoint charge(ChargeUserPointCommand command);
    UserPoint use(UseUserPointCommand command);

    UserPoint getUserPointByUserId(long userId);
    
    List<PointHistory> getHistoriesByUserId(long userId);
}
