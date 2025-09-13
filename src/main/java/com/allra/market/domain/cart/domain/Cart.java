package com.allra.market.domain.cart.domain;

import com.allra.market.common.entity.BaseEntity;
import com.allra.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.allra.market.domain.product.domain.Product;
import com.allra.market.domain.user.domain.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "carts")
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    /* 정적 팩토리 메서드 */
    public static Cart create(final User user) {
        Cart cart = new Cart();
        cart.user = user;
        return cart;
    }

    public void addCartItem(final Product product, final CartItemAddServiceRequest request) {
        CartItem cartItem = CartItem.create(this, product, request.quantity());
        cartItems.add(cartItem);
    }

}
