package com.allra.market.domain.cart.interfaces.request;

import com.allra.market.domain.cart.application.request.CartItemUpdateServiceRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemUpdateRequest(

        @NotNull(message = "수량을 선택해주세요.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        Integer quantity
) {
        public CartItemUpdateServiceRequest toServiceRequest() {
                return new CartItemUpdateServiceRequest(quantity);
        }
}
