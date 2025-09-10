package com.allra.market.domain.cart.interfaces;

import com.allra.market.domain.cart.application.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart/me")
    public void findCart() {
        cartService.findCart();
    }
}
