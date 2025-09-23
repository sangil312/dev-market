package com.dev.market.domain.cart.application.request;


public record CartItemAddServiceRequest(
        Long productId,
        Integer quantity
) {
}
