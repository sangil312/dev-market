package com.allra.market.domain.order.application;

import com.allra.market.common.exception.NotFoundException;
import com.allra.market.common.exception.QuantityOverException;
import com.allra.market.domain.cart.domain.Cart;
import com.allra.market.domain.cart.domain.CartItem;
import com.allra.market.domain.cart.domain.repository.CartRepository;
import com.allra.market.domain.order.application.repuest.OrderCreateServiceRequest;
import com.allra.market.domain.order.application.response.OrderCreateResponse;
import com.allra.market.domain.order.domain.Order;
import com.allra.market.domain.order.domain.OrderItem;
import com.allra.market.domain.order.domain.repository.OrderRepository;
import com.allra.market.domain.product.domain.Product;
import com.allra.market.domain.product.domain.repository.ProductRepository;
import com.allra.market.domain.user.domain.User;
import com.allra.market.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.allra.market.common.exception.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    @Transactional
    public OrderCreateResponse createOrderAndProductStockDecreases(
            final Long userId,
            final OrderCreateServiceRequest request,
            final LocalDateTime createdAt
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Cart cart = cartRepository.findCartWithProductsByUserIdAndCartId(userId, request.cartId())
                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND));

        List<CartItem> cartItems = cartItemsExtract(cart, request.cartItemIds());

        productDecreasesStock(cartItems);

        Order order = Order.create(user, cartItems, createdAt);
        orderRepository.save(order);

        return OrderCreateResponse.of(userId, request.cartId(), order, cartItems);
    }

    private List<CartItem> cartItemsExtract(Cart cart, List<Long> requestCartItemIds) {
        Map<Long, CartItem> cartItemMap = cart.getCartItems().stream()
                .collect(Collectors.toMap(CartItem::getId, ci -> ci));

        return requestCartItemIds.stream()
                .map(requestItemId -> Optional.ofNullable(cartItemMap.get(requestItemId))
                                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND)))
                .toList();
    }


    private void productDecreasesStock(List<CartItem> cartItems) {
        // 장바구니 상품 ID 추출
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

    @Transactional
    public void productStockRollback(Long orderId) {
        Order order = orderRepository.findAllWithProductsById(orderId)
                .orElseThrow(() -> new NotFoundException(PAYMENT_FAILED));

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.increaseQuantity(orderItem.getQuantity());
        }
    }
}
