package com.allra.market.domain.product.repository;

import com.allra.market.domain.product.request.ProductSearchCondition;
import com.allra.market.domain.product.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductResponse> search(ProductSearchCondition condition, Pageable pageable);
}
