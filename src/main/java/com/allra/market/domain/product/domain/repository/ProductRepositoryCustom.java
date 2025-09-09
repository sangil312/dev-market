package com.allra.market.domain.product.domain.repository;

import com.allra.market.domain.product.application.request.ProductSearchCondition;
import com.allra.market.domain.product.interfaces.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductResponse> search(ProductSearchCondition condition, Pageable pageable);
}
