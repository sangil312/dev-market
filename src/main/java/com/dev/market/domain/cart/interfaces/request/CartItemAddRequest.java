package com.dev.market.domain.cart.interfaces.request;

import com.dev.market.domain.cart.application.request.CartItemAddServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemAddRequest(

        @NotNull(message = "상품 ID가 존재하지 않습니다.")
        @Positive(message = "상품 ID가 존재하지 않습니다.")
        Long productId,

        @NotNull(message = "수량은 1개 이상이어야 합니다.")
        @Positive(message = "수량은 1개 이상이어야 합니다.")
        Integer quantity
) {
        public CartItemAddServiceRequest toServiceRequest() {
                return new CartItemAddServiceRequest(productId, quantity);
        }
}
