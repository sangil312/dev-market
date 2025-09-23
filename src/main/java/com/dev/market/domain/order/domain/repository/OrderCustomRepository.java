package com.dev.market.domain.order.domain.repository;

import com.dev.market.domain.order.domain.Order;

import java.util.Optional;

public interface OrderCustomRepository {

    Optional<Order> findAllWithProductsById(Long orderId);
}
