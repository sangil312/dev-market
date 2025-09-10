package com.allra.market.domain.cart.application;

import com.allra.market.domain.cart.application.dto.CartItemResponse;
import com.allra.market.domain.cart.application.dto.CartResponse;
import com.allra.market.domain.cart.domain.Cart;
import com.allra.market.domain.cart.domain.repository.CartRepository;
import com.allra.market.domain.user.domain.User;
import com.allra.market.domain.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Transactional
    public void findCart() {
        // 토큰 or 세션에서 user_id 추출했다고 가정
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 장바구니는 사용자 생성 시점이 아닌 조회 시점에 생성
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.createCart(user)));

        List<CartItemResponse> cartItemResponseList = cartRepository.findCart(cart.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 담긴 상품이 없습니다."));

        CartResponse.of(cart.getId(), cartItemResponseList);
    }
}
