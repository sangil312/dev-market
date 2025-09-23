package com.dev.market.domain.cart.interfaces;

import com.dev.market.domain.cart.application.CartService;
import com.dev.market.domain.cart.application.response.CartAddResponse;
import com.dev.market.domain.cart.interfaces.request.CartItemAddRequest;
import com.dev.market.domain.cart.application.response.CartItemResponse;
import com.dev.market.domain.cart.interfaces.request.CartItemUpdateRequest;
import com.dev.market.domain.cart.application.response.CartResponse;
import com.dev.market.domain.cart.interfaces.request.CartItemDeleteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/api/carts/me")
    public ResponseEntity<CartResponse> findCart() {
        return ResponseEntity.ok(cartService.findCart(1L));
    }

    @PostMapping("/api/carts/items")
    public ResponseEntity<CartAddResponse> addCartItem(
            @Valid @RequestBody CartItemAddRequest request
    ) {
        return ResponseEntity.ok(cartService.addCartItem(1L, request.toServiceRequest()));
    }

    @PatchMapping("/api/carts/{cartId}/items/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemUpdateRequest request
    ) {
        return ResponseEntity.ok(
                cartService.updateCartItem(1L, cartId, cartItemId, request.toServiceRequest()));
    }

    @DeleteMapping("/api/carts/{cartId}/items")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemDeleteRequest request
    ) {
        cartService.deleteCartItem(1L, cartId, request.toServiceRequest());
        return ResponseEntity.ok().build();
    }
}
