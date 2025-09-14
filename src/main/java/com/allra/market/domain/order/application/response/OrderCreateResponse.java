package com.allra.market.domain.order.application.response;

import com.allra.market.domain.cart.domain.CartItem;
import com.allra.market.domain.order.domain.Order;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreateResponse(
        Long userId,
        Long cartId,
        Order order,
        List<CartItem> cartItems
) {
    public static OrderCreateResponse of(Long userId, Long cartId, Order order, List<CartItem> cartItems) {
        return OrderCreateResponse.builder()
                .userId(userId)
                .cartId(cartId)
                .order(order)
                .cartItems(cartItems)
                .build();
    }
}
