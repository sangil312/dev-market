package com.dev.market.domain.cart.domain;

import static com.dev.market.common.exception.enums.ErrorCode.CART_ITEM_NOT_FOUND;

import com.dev.market.common.entity.BaseEntity;
import com.dev.market.common.exception.NotFoundException;
import com.dev.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.user.domain.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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

    /* 비지니스 메서드 */
    public void addCartItem(final Product product, final CartItemAddServiceRequest request) {
        CartItem cartItem = CartItem.create(this, product, request.quantity());
        cartItems.add(cartItem);
    }

    public List<CartItem> cartItemsExtract(List<Long> requestCartItemIds) {
        Map<Long, CartItem> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(CartItem::getId, cartItem -> cartItem));

        return requestCartItemIds.stream()
                .map(requestCartItemId ->
                        Optional.ofNullable(cartItemMap.get(requestCartItemId))
                                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND)))
                .collect(Collectors.toList());
    }
}
