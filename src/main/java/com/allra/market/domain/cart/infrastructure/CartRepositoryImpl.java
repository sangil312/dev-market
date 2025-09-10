package com.allra.market.domain.cart.infrastructure;

import static com.allra.market.domain.cart.domain.QCartItem.cartItem;
import static com.allra.market.domain.product.domain.QProduct.product;

import com.allra.market.domain.cart.application.dto.CartItemResponse;
import com.allra.market.domain.cart.application.dto.QCartItemResponse;
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
    public Optional<List<CartItemResponse>> findCart(Long cartId) {

        List<CartItemResponse> content = queryFactory
                .select(new QCartItemResponse(
                        cartItem.id,
                        product.id,
                        product.name,
                        cartItem.quantity,
                        cartItem.unitPrice,
                        product.quantity.eq(0),
                        cartItem.quantity.gt(product.quantity)
                ))
                .from(cartItem)
                .join(cartItem.product, product)
                .where(cartItem.cart.id.eq(cartId))
                .fetch();

        return content == null ? Optional.empty() : Optional.of(content);
    }
}
