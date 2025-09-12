package com.allra.market.domain.cart.interfaces;

import com.allra.market.domain.cart.application.CartService;
import com.allra.market.domain.cart.application.dto.response.CartAddResponse;
import com.allra.market.domain.cart.application.dto.request.AddCartItemRequest;
import com.allra.market.domain.cart.application.dto.response.CartItemResponse;
import com.allra.market.domain.cart.application.dto.request.UpdateCartItemRequest;
import com.allra.market.domain.cart.application.dto.response.CartResponse;
import com.allra.market.domain.cart.application.dto.request.DeleteCartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/carts/me")
    public ResponseEntity<CartResponse> findCart() {
        return ResponseEntity.ok(cartService.findCart(1L));
    }

    @PostMapping("/carts/items")
    public ResponseEntity<CartAddResponse> addCartItem(
            @Validated @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.ok(cartService.addCartItem(1L, request));
    }

    @PatchMapping("/carts/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long cartItemId,
            @Validated @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(cartService.updateCartItem(1L, cartId, cartItemId, request));
    }

    @DeleteMapping("/carts/{cartId}/items")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Long cartId,
            @Validated @RequestBody DeleteCartItemRequest request
    ) {
        cartService.deleteCartItem(1L, cartId, request);
        return ResponseEntity.ok().build();
    }
}
