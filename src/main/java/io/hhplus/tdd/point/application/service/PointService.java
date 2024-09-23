package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import io.hhplus.tdd.point.application.dto.UseUserPointCommand;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;

public interface PointService {
    UserPoint charge(ChargeUserPointCommand command);
    UserPoint use(UseUserPointCommand command);
}
