package com.allra.market.domain.product.infrastructure.response;

import com.allra.market.domain.payment.application.response.PaymentResultDto;

public record PaymentResponse(
        String status,
        String transactionId,
        String message
) {
    public PaymentResultDto toDto(Long orderId, Long totalPrice) {
        return PaymentResultDto.builder()
                .orderId(orderId)
                .totalPrice(totalPrice)
                .success(status.equalsIgnoreCase("SUCCESS"))
                .transactionId(transactionId)
                .message(message)
                .build();
    }
}
