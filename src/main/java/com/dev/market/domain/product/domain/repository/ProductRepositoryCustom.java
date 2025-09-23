package com.dev.market.domain.product.domain.repository;

import com.dev.market.domain.product.application.request.ProductSearchCondition;
import com.dev.market.domain.product.application.response.ProductResponse;
import com.dev.market.domain.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {
    Page<ProductResponse> search(ProductSearchCondition condition, Pageable pageable);

    List<Product> findAllWithPessimisticLockByIdIn(List<Long> productIds);
}
