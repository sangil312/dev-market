package com.allra.market.domain.cart.domain.repository;

import com.allra.market.domain.cart.application.dto.CartItemResponse;
import java.util.List;
import java.util.Optional;

public interface CartRepositoryCustom {
    Optional<List<CartItemResponse>> findCart(Long cartId);
}
