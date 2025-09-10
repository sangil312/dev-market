package com.allra.market.domain.product.application.dto.request;

import lombok.Builder;

@Builder
public record ProductSearchCondition(

        Long categoryId,

        String productName,

        Long minPrice,

        Long maxPrice
) {
}
