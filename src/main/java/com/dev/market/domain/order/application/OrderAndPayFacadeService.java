package com.dev.market.domain.order.application;

import com.dev.market.common.exception.dto.ExternalApiException;
import com.dev.market.domain.cart.application.CartService;
import com.dev.market.domain.cart.application.request.CartItemDeleteServiceRequest;
import com.dev.market.domain.cart.domain.CartItem;
import com.dev.market.domain.order.application.repuest.OrderCreateServiceRequest;
import com.dev.market.domain.order.application.response.OrderCreateResponse;
import com.dev.market.domain.order.application.response.OrderResponse;
import com.dev.market.domain.order.domain.Order;
import com.dev.market.domain.payment.application.PaymentService;
import com.dev.market.domain.payment.application.response.PaymentResultDto;
import com.dev.market.domain.payment.domain.Payment;
import com.dev.market.domain.product.application.ProductService;
import com.dev.market.domain.product.infrastructure.PaymentApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderAndPayFacadeService {

    private final OrderService orderService;

    private final PaymentService paymentService;

    private final ProductService productService;

    private final CartService cartService;

    private final PaymentApiService paymentApiService;

    public OrderResponse createOrderAndPay(
            final String idempotencyKey,
            final Long userId,
            final OrderCreateServiceRequest request,
            final LocalDateTime createdAt
    ) {
        // 주문 생성 및 상품 재고 차감
        OrderCreateResponse orderCreateResponse = orderService.createOrderAndProductStockDecreases(
                idempotencyKey, userId, request, createdAt);

        Order createdOrder = orderCreateResponse.order();

        PaymentResultDto paymentResult;
        try {
            paymentResult = paymentApiService.externalPaymentApiCall(
                    createdOrder.getId(),
                    createdOrder.getTotalPrice());
            // 결제 완료 장바구니 상품 삭제
            paymentSuccessCartItemsDelete(orderCreateResponse);
        } catch (ExternalApiException e) {
            paymentResult = PaymentResultDto.failed(
                    createdOrder.getId(),
                    createdOrder.getTotalPrice());
            // 재고 롤백
            productService.productStockRollback(createdOrder.getOrderItems());
        }
        // 결제 이력 추가 및 주문 상태 변경
        Payment payment = paymentService.createPaymentAndOrderStatusUpdate(createdOrder, paymentResult);

        return OrderResponse.of(createdOrder.getId(), payment.getId(), paymentResult.success());
    }

    private void paymentSuccessCartItemsDelete(OrderCreateResponse orderCreateResponse) {
        List<Long> cartItemIds = orderCreateResponse.cartItems().stream()
                .map(CartItem::getId)
                .toList();

        CartItemDeleteServiceRequest cartItemDeleteServiceRequest = new CartItemDeleteServiceRequest(cartItemIds);

        cartService.deleteCartItem(orderCreateResponse.userId(), orderCreateResponse.cartId(), cartItemDeleteServiceRequest);
    }
}
