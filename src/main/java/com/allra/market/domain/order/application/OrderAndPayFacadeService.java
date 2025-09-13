package com.allra.market.domain.order.application;

import com.allra.market.common.exception.dto.ExternalApiException;
import com.allra.market.domain.order.application.repuest.OrderCreateServiceRequest;
import com.allra.market.domain.order.application.response.OrderResponse;
import com.allra.market.domain.order.domain.Order;
import com.allra.market.domain.payment.application.PaymentService;
import com.allra.market.domain.payment.application.response.PaymentResultDto;
import com.allra.market.domain.payment.domain.Payment;
import com.allra.market.domain.product.infrastructure.PaymentApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderAndPayFacadeService {

    private final OrderService orderService;

    private final PaymentService paymentService;

    private final PaymentApiService paymentApiService;

    public OrderResponse createOrderAndPay(
            final Long userId,
            final OrderCreateServiceRequest request,
            final LocalDateTime createdAt
    ) {
        // 주문 생성 및 상품 재고 차감
        Order createdOrder = orderService.createOrderAndProductStockDecreases(userId, request, createdAt);

        PaymentResultDto paymentResult;
        try {
            paymentResult = paymentApiService.externalPaymentApiCall(
                    createdOrder.getId(),
                    createdOrder.getTotalPrice());
        } catch (ExternalApiException e) {
            paymentResult = PaymentResultDto.failed(
                    createdOrder.getId(),
                    createdOrder.getTotalPrice());
            // 재고 롤백
            orderService.productStockRollback(createdOrder.getId());
        }
        // 결제 이력 추가 및 주문 상태 변경
        Payment payment = paymentService.createPaymentAndOrderStatusUpdate(createdOrder, paymentResult);

        return OrderResponse.of(createdOrder.getId(), payment.getId(), paymentResult.success());
    }
}
