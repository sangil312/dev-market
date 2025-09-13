package com.allra.market.domain.cart.domain.repository;

import com.allra.market.domain.cart.domain.Cart;
import com.allra.market.domain.cart.domain.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartRepositoryCustom {

    List<CartItem> findCartItemsByCartId(Long cartId);

    Optional<Cart> findCartByUserId(Long userId);

    Optional<CartItem> findCartItem(Long userId, Long cartId, Long cartItemId);

    void deleteCartItems(Long userId, Long cartId, List<Long> cartItemId);

    Optional<Cart> findCartWithProductsByUserIdAndCartId(Long userId, Long cartId);
}
