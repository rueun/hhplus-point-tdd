package io.hhplus.tdd.point.domain.repository;

import io.hhplus.tdd.point.domain.model.entity.UserPoint;

public interface UserPointRepository {
    UserPoint save(UserPoint userPoint);
    UserPoint findByUserId(long userId);
}
