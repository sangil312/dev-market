package com.dev.market.domain.payment.application.response;

import lombok.Builder;

@Builder
public record PaymentResultDto(
        Long orderId,
        Long totalPrice,
        boolean success,
        String transactionId,
        String message
) {
    public static PaymentResultDto failed(Long orderId, Long totalPrice) {
        return PaymentResultDto.builder()
                .orderId(orderId)
                .totalPrice(totalPrice)
                .success(false)
                .transactionId(null)
                .message(null)
                .build();
    }
}
