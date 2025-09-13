package com.allra.market.domain.cart.domain;

import com.allra.market.common.entity.BaseEntity;
import com.allra.market.domain.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Integer quantity;

    private Long unitPrice;

    /* 정적 팩토리 메서드 */
    public static CartItem create(Cart cart, Product product, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.cart = cart;
        cartItem.product = product;
        cartItem.quantity = quantity;
        cartItem.unitPrice = product.getPrice();
        return cartItem;
    }

    /* 비지니스 메서드 */
    public void updateQuantity(final Integer quantity) {
        this.quantity = quantity;
    }
}
