package com.dev.market.domain.cart.domain.repository;

import com.dev.market.domain.cart.domain.Cart;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {
    Optional<Cart> findByUserId(Long userId);
}
