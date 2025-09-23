package com.dev.market.domain.order.application.repuest;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreateServiceRequest(
        Long cartId,
        List<Long> cartItemIds
) {
}
