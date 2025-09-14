package com.allra.market.domain.order.domain.repository;

import com.allra.market.domain.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {

    boolean existsByIdempotencyKey(String idempotencyKey);
}
