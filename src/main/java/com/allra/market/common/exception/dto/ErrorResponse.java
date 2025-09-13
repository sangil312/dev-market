package com.allra.market.common.exception.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String error,
        int statusCode,
        String message
) {
    public static ErrorResponse of(
            final HttpStatus httpStatus,
            final String message
    ) {
        return new ErrorResponse(
                httpStatus.getReasonPhrase(),
                httpStatus.value(),
                message
        );
    }
}
