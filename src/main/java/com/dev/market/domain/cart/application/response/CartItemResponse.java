package com.dev.market.domain.cart.application.response;

import com.dev.market.domain.cart.domain.CartItem;
import com.dev.market.domain.product.domain.Product;
import lombok.Builder;

@Builder
public record CartItemResponse(
        Long cartItemId,
        Long productId,
        String productName,
        Integer quantity,
        Long unitPrice,
        Long subTotalPrice,
        Boolean isSoldOut,
        Boolean quantityOver
) {
    public static CartItemResponse of(final CartItem item) {
        Product product = item.getProduct();
        return CartItemResponse.builder()
                .cartItemId(item.getCart().getId())
                .productId(product.getId())
                .productName(product.getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subTotalPrice(item.getUnitPrice() * item.getQuantity())
                .isSoldOut(product.getQuantity() == 0)
                .quantityOver(product.isQuantityLessThan(item.getQuantity()))
                .build();
    }
}
