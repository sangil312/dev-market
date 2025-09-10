package com.allra.market.domain.cart.infrastructure;

import static com.allra.market.domain.cart.domain.QCart.cart;
import static com.allra.market.domain.cart.domain.QCartItem.cartItem;
import static com.allra.market.domain.product.domain.QProduct.product;

import com.allra.market.domain.cart.domain.Cart;
import com.allra.market.domain.cart.domain.CartItem;
import com.allra.market.domain.cart.domain.repository.CartRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItem> findCartItemsByCartId(Long cartId) {

        return queryFactory
                .select(cartItem)
                .from(cartItem)
                .join(cartItem.product).fetchJoin()
                .where(cartItem.cart.id.eq(cartId))
                .fetch();
    }

    @Override
    public Optional<Cart> findCartByUserId(Long userId) {
        Cart result = queryFactory
                .selectFrom(cart)
                .leftJoin(cart.cartItemList, cartItem).fetchJoin()
                .leftJoin(cartItem.product).fetchJoin()
                .where(cart.user.id.eq(userId))
                .fetchOne();

        return result == null ? Optional.empty() : Optional.of(result);
    }

    @Override
    public Optional<CartItem> findCartItem(Long userId, Long cartId, Long cartItemId) {
        CartItem result = queryFactory
                .selectFrom(cartItem)
                .join(cartItem.cart, cart)
                .join(cartItem.product, product).fetchJoin()
                .where(
                        cartItem.id.eq(cartItemId),
                        cart.id.eq(cartId),
                        cart.user.id.eq(userId)
                )
                .fetchOne();

        return result == null ? Optional.empty() : Optional.of(result);
    }

    @Override
    public void deleteCartItems(Long userId, Long cartId, List<Long> cartItemIds) {
        queryFactory
                .delete(cartItem)
                .where(
                        cartItem.cart.id.eq(cartId),
                        cartItem.cart.user.id.eq(userId),
                        cartItem.id.in(cartItemIds)
                )
                .execute();
    }
}
