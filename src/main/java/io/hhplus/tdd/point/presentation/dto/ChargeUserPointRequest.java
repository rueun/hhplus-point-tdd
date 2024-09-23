package io.hhplus.tdd.point.presentation.dto;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChargeUserPointRequest {
    private long amount;

    public ChargeUserPointCommand toCommand(final long userId) {
        return ChargeUserPointCommand.of(userId, amount);
    }
}
