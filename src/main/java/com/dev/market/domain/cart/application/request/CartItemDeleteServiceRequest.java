package com.dev.market.domain.cart.application.request;


import java.util.List;

public record CartItemDeleteServiceRequest(
        List<Long> cartItemIds
) {
}
