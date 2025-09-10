package com.allra.market.domain.cart.application.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record CartResponse(
        Long cartId,
        Integer totalCount,
        Long totalPrice,
        List<CartItemResponse> cartItemList
) {
    public static CartResponse of(
            final Long cartId,
            final List<CartItemResponse> cartItemResponseList
    ) {
//        cartItemResponseList.stream()
//                .map(cartItemResponse -> )
        CartResponse response = CartResponse.builder()
                .cartId(cartId)
                .build();

        return response;
    }
}
