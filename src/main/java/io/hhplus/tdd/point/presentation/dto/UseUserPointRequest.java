package io.hhplus.tdd.point.presentation.dto;

import io.hhplus.tdd.point.application.dto.UseUserPointCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UseUserPointRequest {
    private long amount;

    public UseUserPointCommand toCommand(final long userId) {
        return UseUserPointCommand.of(userId, amount);
    }
}
