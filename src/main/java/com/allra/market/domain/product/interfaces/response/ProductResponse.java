package com.allra.market.domain.product.interfaces.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record ProductResponse(
        Long productId,
        Long categoryId,
        String productName,
        Long price,
        Integer productQuantity,
        Boolean isSoldOut
) {
    @QueryProjection
    public ProductResponse {
    }
}
