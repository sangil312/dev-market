package com.dev.market.domain.order.domain.repository;

import com.dev.market.domain.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {

    boolean existsByIdempotencyKey(String idempotencyKey);
}
