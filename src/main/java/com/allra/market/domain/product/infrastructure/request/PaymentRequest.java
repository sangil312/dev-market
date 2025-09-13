package com.allra.market.domain.product.infrastructure.request;

public record PaymentRequest(
        String orderId,
        Long amount
) {
}
