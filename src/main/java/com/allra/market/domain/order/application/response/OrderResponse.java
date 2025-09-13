package com.allra.market.domain.order.application.response;

import lombok.Builder;

@Builder
public record OrderResponse(
        long orderId,
        long paymentId,
        boolean success,
        String message
) {
    public static OrderResponse of(Long orderId, Long paymentId, boolean success) {
        return OrderResponse.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .success(success)
                .message(success ? "결제에 성공했습니다." : "결제에 실패했습니다.")
                .build();
    }
}
