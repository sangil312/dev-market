package com.allra.market.domain.order.domain;

import com.allra.market.common.entity.BaseUpdatedAtEntity;
import com.allra.market.domain.cart.domain.CartItem;
import com.allra.market.domain.order.domain.enums.OrderStatus;
import com.allra.market.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseUpdatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String idempotencyKey;

    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime createdAt;

    /* 정적 팩토리 메서드 */
    public static Order create(
            final String idempotencyKey,
            final User user,
            final List<CartItem> cartItems,
            LocalDateTime createdAt
    ) {
        Order order = new Order();
        order.idempotencyKey = idempotencyKey;
        order.user = user;
        order.totalPrice = calculateTotalPrice(cartItems);
        order.status = OrderStatus.CREATED;
        order.createdAt = createdAt;
        order.orderItems = cartItems.stream()
                .map(item -> OrderItem.create(order, item.getProduct(), item.getQuantity()))
                .toList();

        return order;
    }

    private static Long calculateTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToLong(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
    }

    public void updateStatus(boolean paymentSuccess) {
        this.status = paymentSuccess ? OrderStatus.PAID : OrderStatus.PAYMENT_FAILED;
    }
}
