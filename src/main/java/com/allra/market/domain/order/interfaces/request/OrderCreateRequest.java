package com.allra.market.domain.order.interfaces.request;


import com.allra.market.domain.order.application.repuest.OrderCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderCreateRequest(

        @NotNull(message = "장바구니 ID가 존재하지 않습니다.")
        @Positive(message = "장바구니 ID가 존재하지 않습니다.")
        Long cartId,

        @NotEmpty(message = "장바구니에 상품을 선택해주세요.")
        List<Long> cartItemIds
) {
    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .cartId(cartId)
                .cartItemIds(cartItemIds)
                .build();
    }
}
