package com.allra.market.domain.product.domain.repository;

import com.allra.market.domain.product.application.request.ProductSearchCondition;
import com.allra.market.domain.product.application.response.ProductResponse;
import com.allra.market.domain.product.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface ProductRepositoryCustom {
    Page<ProductResponse> search(ProductSearchCondition condition, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findAllWithPessimisticLockByIdIn(List<Long> productIds);
}
