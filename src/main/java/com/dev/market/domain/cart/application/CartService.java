package com.dev.market.domain.cart.application;

import com.dev.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.dev.market.domain.cart.application.request.CartItemDeleteServiceRequest;
import com.dev.market.domain.cart.application.request.CartItemUpdateServiceRequest;
import com.dev.market.domain.cart.application.response.CartAddResponse;
import com.dev.market.domain.cart.application.response.CartItemResponse;
import com.dev.market.domain.cart.application.response.CartResponse;

public interface CartService {
    CartResponse findCart(Long userId);

    CartAddResponse addCartItem(Long userId, CartItemAddServiceRequest request);

    CartItemResponse updateCartItem(Long userId, Long cartId, Long cartItemId, CartItemUpdateServiceRequest request);

    void deleteCartItem(Long userId, Long cartId, CartItemDeleteServiceRequest request);
}
