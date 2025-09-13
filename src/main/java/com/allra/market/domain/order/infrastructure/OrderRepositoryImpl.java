package com.allra.market.domain.order.infrastructure;

import com.allra.market.domain.order.domain.Order;
import com.allra.market.domain.order.domain.repository.OrderCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.allra.market.domain.order.domain.QOrder.order;
import static com.allra.market.domain.order.domain.QOrderItem.orderItem;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Order> findAllWithProductsById(Long orderId) {
        Order result = queryFactory
                .selectFrom(order)
                .join(order.orderItems, orderItem).fetchJoin()
                .join(orderItem.product).fetchJoin()
                .where(order.id.eq(orderId))
                .fetchOne();

        return result == null ? Optional.empty() : Optional.of(result);
    }
}
