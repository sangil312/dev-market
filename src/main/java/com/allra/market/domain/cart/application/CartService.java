package com.allra.market.domain.cart.application;

import com.allra.market.common.exception.NotFoundException;
import com.allra.market.common.exception.QuantityOverException;
import com.allra.market.domain.cart.application.dto.request.AddCartItemRequest;
import com.allra.market.domain.cart.application.dto.request.DeleteCartItemRequest;
import com.allra.market.domain.cart.application.dto.request.UpdateCartItemRequest;
import com.allra.market.domain.cart.application.dto.response.CartAddResponse;
import com.allra.market.domain.cart.application.dto.response.CartItemResponse;
import com.allra.market.domain.cart.application.dto.response.CartResponse;
import com.allra.market.domain.cart.domain.Cart;
import com.allra.market.domain.cart.domain.CartItem;
import com.allra.market.domain.cart.domain.repository.CartRepository;
import com.allra.market.domain.product.domain.Product;
import com.allra.market.domain.product.domain.repository.ProductRepository;
import com.allra.market.domain.user.domain.User;
import com.allra.market.domain.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.allra.market.common.exception.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse findCart(final Long userId) {
        // 토큰 or 세션에서 user_id 추출했다고 가정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.create(user)));

        List<CartItem> cartItems = cartRepository.findCartItemsByCartId(cart.getId());

        List<CartItemResponse> items = cartItems.stream()
                .map(CartItemResponse::of)
                .toList();

        return CartResponse.of(cart.getId(), items);
    }

    @Transactional
    public CartAddResponse addCartItem(final Long userId, final AddCartItemRequest request) {
        // 토큰 or 세션에서 user_id 추출했다고 가정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

        Cart cart = cartRepository.findCartByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.create(user)));

        Optional<CartItem> existCartItem = cart.getCartItemList().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst();

        Integer itemQuantity = existCartItem.map(CartItem::getQuantity).orElse(0);
        int totalQuantity = itemQuantity + request.quantity();

        if (product.isQuantityOver(totalQuantity)) {
            throw new QuantityOverException(PRODUCT_QUANTITY_OVER);
        }

        existCartItem.ifPresentOrElse(
                item -> item.updateQuantity(request.quantity()),
                () -> cart.addCartItem(product, request)
        );

        return CartAddResponse.of(cart.getId(), cart.getCartItemList().size());
    }

    @Transactional
    public CartItemResponse updateCartItem(
            final Long userId,
            final Long cartId,
            final Long cartItemId,
            final UpdateCartItemRequest request
    ) {
        // 토큰 or 세션에서 user_id 추출했다고 가정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        CartItem item = cartRepository.findCartItem(user.getId(), cartId, cartItemId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

        if (item.getProduct().isQuantityOver(request.quantity())) {
            throw new QuantityOverException(PRODUCT_QUANTITY_OVER);
        }

        item.updateQuantity(request.quantity());

        return CartItemResponse.of(item);
    }

    @Transactional
    public void deleteCartItem(final Long userId, final Long cartId, final DeleteCartItemRequest request) {
        // 토큰 or 세션에서 user_id 추출했다고 가정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        cartRepository.deleteCartItems(user.getId(), cartId, request.cartItemIds());
    }
}
