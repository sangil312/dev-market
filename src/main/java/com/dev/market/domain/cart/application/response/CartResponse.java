package com.dev.market.domain.cart.application.response;

import java.util.List;
import lombok.Builder;

@Builder
public record CartResponse(
        long cartId,
        int totalItemsQuantity,
        long totalPrice,
        List<CartItemResponse> items
) {
    public static CartResponse of(
            final long cartId,
            final List<CartItemResponse> items
    ) {
        long calculateTotalPrice = items.stream()
                .mapToLong(item -> item.unitPrice() * item.quantity())
                .sum();

        return CartResponse.builder()
                .cartId(cartId)
                .totalItemsQuantity(items.size())
                .totalPrice(calculateTotalPrice)
                .items(items)
                .build();
    }
}
