package io.hhplus.tdd.common.model;

public record ErrorResponse(
        String code,
        String message
) {
}
