package com.dev.market.domain.order.application;

import com.dev.market.common.exception.AlreadyOrderCompletedException;
import com.dev.market.common.exception.NotFoundException;
import com.dev.market.domain.cart.application.CartService;
import com.dev.market.domain.cart.domain.CartItem;
import com.dev.market.domain.order.application.repuest.OrderCreateServiceRequest;
import com.dev.market.domain.order.application.response.OrderCreateResponse;
import com.dev.market.domain.order.domain.Order;
import com.dev.market.domain.order.domain.repository.OrderRepository;
import com.dev.market.domain.product.application.ProductService;
import com.dev.market.domain.user.domain.User;
import com.dev.market.domain.user.domain.repository.UserRepository;
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

    private final ProductService productService;

    private final CartService cartService;

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

        List<CartItem> cartItems = cartService.cartItemsExtract(userId, request.cartId(), request.cartItemIds());

        productService.productDecreasesStock(cartItems);

        Order order = Order.create(idempotencyKey, user, cartItems, createdAt);
        orderRepository.save(order);

        return OrderCreateResponse.of(userId, request.cartId(), order, cartItems);
    }
}
