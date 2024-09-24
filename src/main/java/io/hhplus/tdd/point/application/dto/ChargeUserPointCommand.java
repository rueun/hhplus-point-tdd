package io.hhplus.tdd.point.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChargeUserPointCommand {
    private final long userId;
    private final long amount;

    public static ChargeUserPointCommand of(final long userId, final long amount) {
        return new ChargeUserPointCommand(userId, amount);
    }
}