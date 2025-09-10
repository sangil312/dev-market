package com.allra.market.domain.cart.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record CartItemResponse(
        Long cartItemId,
        Long productId,
        String productName,
        Integer quantity,
        Long unitPrice,
        Boolean isSoldOut,
        Boolean quantityOver
) {
    @QueryProjection
    public CartItemResponse {}
}
