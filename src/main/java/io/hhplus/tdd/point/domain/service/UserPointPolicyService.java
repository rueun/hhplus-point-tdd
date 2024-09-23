package io.hhplus.tdd.point.domain.service;

import io.hhplus.tdd.point.domain.model.entity.UserPoint;

public interface UserPointPolicyService {
    void validateCharge(UserPoint userPoint, long amount);
    void validateUse(UserPoint userPoint, long amount);
}