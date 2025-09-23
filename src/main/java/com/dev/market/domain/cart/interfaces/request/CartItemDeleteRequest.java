package com.dev.market.domain.cart.interfaces.request;

import com.dev.market.domain.cart.application.request.CartItemDeleteServiceRequest;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CartItemDeleteRequest(

        @NotEmpty(message = "삭제할 상품을 선택해주세요.")
        List<Long> cartItemIds
) {
        public CartItemDeleteServiceRequest toServiceRequest() {
                return new CartItemDeleteServiceRequest(cartItemIds);
        }
}
