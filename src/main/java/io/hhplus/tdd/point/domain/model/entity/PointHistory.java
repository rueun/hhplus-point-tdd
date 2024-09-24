package io.hhplus.tdd.point.domain.model.entity;

import io.hhplus.tdd.point.domain.model.vo.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
