package com.allra.market.domain.cart.application.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteCartItemRequest(

        @NotEmpty(message = "삭제할 상품을 선택해주세요.")
        List<Long> cartItemIds
) {
}
