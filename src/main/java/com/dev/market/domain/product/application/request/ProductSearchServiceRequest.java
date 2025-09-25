package com.dev.market.domain.product.application.request;

import lombok.Builder;

@Builder
public record ProductSearchServiceRequest(
        Long categoryId,
        String productName,
        Long minPrice,
        Long maxPrice
) {
}
