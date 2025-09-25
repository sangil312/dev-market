package com.dev.market.domain.product.interfaces.request;

import com.dev.market.domain.product.application.request.ProductSearchServiceRequest;
import lombok.Builder;

@Builder
public record ProductSearchRequest(
        Long categoryId,
        String productName,
        Long minPrice,
        Long maxPrice
) {
    public ProductSearchServiceRequest toServiceRequest() {
        return ProductSearchServiceRequest.builder()
                .categoryId(categoryId)
                .productName(productName)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
    }
}
