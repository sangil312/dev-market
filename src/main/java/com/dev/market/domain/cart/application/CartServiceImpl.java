package com.dev.market.domain.cart.application;

import com.dev.market.common.exception.NotFoundException;
import com.dev.market.common.exception.QuantityOverException;
import com.dev.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.dev.market.domain.cart.application.request.CartItemDeleteServiceRequest;
import com.dev.market.domain.cart.application.request.CartItemUpdateServiceRequest;
import com.dev.market.domain.cart.application.response.CartAddResponse;
import com.dev.market.domain.cart.application.response.CartItemResponse;
import com.dev.market.domain.cart.application.response.CartResponse;
import com.dev.market.domain.cart.domain.Cart;
import com.dev.market.domain.cart.domain.CartItem;
import com.dev.market.domain.cart.domain.repository.CartRepository;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.product.domain.repository.ProductRepository;
import com.dev.market.domain.user.domain.User;
import com.dev.market.domain.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.market.common.exception.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    @Transactional
    @Override
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
    @Override
    public CartAddResponse addCartItem(final Long userId, final CartItemAddServiceRequest request) {
        // 토큰 or 세션에서 user_id 추출했다고 가정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

        Cart cart = cartRepository.findCartByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.create(user)));

        Optional<CartItem> existCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst();

        Integer itemQuantity = existCartItem.map(CartItem::getQuantity).orElse(0);
        int totalQuantity = itemQuantity + request.quantity();

        if (product.isQuantityLessThan(totalQuantity)) {
            throw new QuantityOverException(PRODUCT_QUANTITY_OVER);
        }

        existCartItem.ifPresentOrElse(
                item -> item.updateQuantity(request.quantity()),
                () -> cart.addCartItem(product, request)
        );

        return CartAddResponse.of(cart.getId(), cart.getCartItems().size());
    }

    @Transactional
    @Override
    public CartItemResponse updateCartItem(
            final Long userId,
            final Long cartId,
            final Long cartItemId,
            final CartItemUpdateServiceRequest request
    ) {
        // 토큰 or 세션에서 user_id 추출했다고 가정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        CartItem item = cartRepository.findCartItem(user.getId(), cartId, cartItemId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

        if (item.getProduct().isQuantityLessThan(request.quantity())) {
            throw new QuantityOverException(PRODUCT_QUANTITY_OVER);
        }

        item.updateQuantity(request.quantity());

        return CartItemResponse.of(item);
    }

    @Transactional
    @Override
    public void deleteCartItem(
            final Long userId,
            final Long cartId,
            final CartItemDeleteServiceRequest request
    ) {
        // 토큰 or 세션에서 user_id 추출했다고 가정
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        cartRepository.deleteCartItems(user.getId(), cartId, request.cartItemIds());
    }
}
