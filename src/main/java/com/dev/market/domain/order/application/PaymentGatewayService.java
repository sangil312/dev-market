package com.dev.market.domain.order.application;

import com.dev.market.domain.payment.application.response.PaymentResultDto;

public interface PaymentGatewayService {
    PaymentResultDto externalPaymentApiCall(Long orderId, Long totalPrice);
}
