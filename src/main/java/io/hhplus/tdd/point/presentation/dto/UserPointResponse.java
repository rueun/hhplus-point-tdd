package io.hhplus.tdd.point.presentation.dto;

import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPointResponse {
    private long id;
    private long point;
    private long updateMillis;

    public static UserPointResponse of(final UserPoint userPoint) {
        return UserPointResponse.builder()
                .id(userPoint.id())
                .point(userPoint.point())
                .updateMillis(userPoint.updateMillis())
                .build();
    }
}
