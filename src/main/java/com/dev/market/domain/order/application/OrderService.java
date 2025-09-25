package com.dev.market.domain.order.application;

import com.dev.market.common.exception.AlreadyOrderCompletedException;
import com.dev.market.common.exception.NotFoundException;
import com.dev.market.common.exception.QuantityOverException;
import com.dev.market.domain.cart.domain.Cart;
import com.dev.market.domain.cart.domain.CartItem;
import com.dev.market.domain.cart.domain.repository.CartRepository;
import com.dev.market.domain.order.application.repuest.OrderCreateServiceRequest;
import com.dev.market.domain.order.application.response.OrderCreateResponse;
import com.dev.market.domain.order.domain.Order;
import com.dev.market.domain.order.domain.repository.OrderRepository;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.product.domain.repository.ProductRepository;
import com.dev.market.domain.user.domain.User;
import com.dev.market.domain.user.domain.repository.UserRepository;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.dev.market.common.exception.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    @Transactional
    public OrderCreateResponse createOrderAndProductStockDecreases(
            final String idempotencyKey,
            final Long userId,
            final OrderCreateServiceRequest request,
            final LocalDateTime createdAt
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        boolean alreadyOrderCompleted = orderRepository.existsByIdempotencyKey(idempotencyKey);
        if (alreadyOrderCompleted) {
            throw new AlreadyOrderCompletedException(ORDER_COMPLETED);
        }

        List<CartItem> cartItems = cartItemsExtract(userId, request.cartId(), request.cartItemIds());

        productDecreasesStock(cartItems);

        Order order = Order.create(idempotencyKey, user, cartItems, createdAt);
        orderRepository.save(order);

        return OrderCreateResponse.of(userId, request.cartId(), order, cartItems);
    }

    private List<CartItem> cartItemsExtract(Long userId, Long cartId, List<Long> requestCartItemIds) {
        Cart cart = cartRepository.findCartWithProductsByUserIdAndCartId(userId, cartId)
                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND));

        return cart.cartItemsExtract(requestCartItemIds);
    }

    private void productDecreasesStock(List<CartItem> cartItems) {
        List<Long> productIds = cartItems.stream()
                .map(item -> item.getProduct().getId())
                .toList();

        List<Product> requestProducts = productRepository.findAllWithPessimisticLockByIdIn(productIds);
        Map<Long, Product> productMap = requestProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 재고 차감
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProduct().getId());
            if (Objects.isNull(product)) {
                throw new NotFoundException(PRODUCT_NOT_FOUND);
            }
            if (product.isQuantityLessThan(item.getQuantity())) {
                throw new QuantityOverException(PRODUCT_QUANTITY_OVER);
            }
            product.decreaseQuantity(item.getQuantity());
        }
    }
}
