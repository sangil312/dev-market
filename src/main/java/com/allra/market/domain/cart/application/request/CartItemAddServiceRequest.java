package com.allra.market.domain.cart.application.request;


public record CartItemAddServiceRequest(
        Long productId,
        Integer quantity
) {
}
