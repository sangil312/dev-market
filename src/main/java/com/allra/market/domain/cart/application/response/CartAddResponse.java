package com.allra.market.domain.cart.application.response;

public record CartAddResponse(
        Long cartId,
        int cartBadgeCount
) {
    public static CartAddResponse of(final Long cartId, final int cartBadgeCount) {
        return new CartAddResponse(cartId, cartBadgeCount);
    }
}
