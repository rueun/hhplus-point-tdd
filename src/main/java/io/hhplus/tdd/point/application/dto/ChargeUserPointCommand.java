package io.hhplus.tdd.point.application.dto;

import lombok.Getter;

@Getter
public class ChargeUserPointCommand {
    private final long userId;
    private final long amount;

    private ChargeUserPointCommand(final long userId, final long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount should be positive");
        }
        this.userId = userId;
        this.amount = amount;
    }

    public static ChargeUserPointCommand of(final long userId, final long amount) {
        return new ChargeUserPointCommand(userId, amount);
    }
}