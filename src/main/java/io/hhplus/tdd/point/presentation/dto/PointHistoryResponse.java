package io.hhplus.tdd.point.presentation.dto;

import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.vo.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistoryResponse {
    private long id;
    private long userId;
    private long amount;
    private TransactionType type;
    private long updateMillis;

    public static PointHistoryResponse of(final PointHistory pointHistory) {
        return PointHistoryResponse.builder()
                .id(pointHistory.id())
                .userId(pointHistory.userId())
                .amount(pointHistory.amount())
                .type(pointHistory.type())
                .updateMillis(pointHistory.updateMillis())
                .build();
    }
}
