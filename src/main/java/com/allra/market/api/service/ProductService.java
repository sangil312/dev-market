package com.allra.market.api.service;

import com.allra.market.domain.product.request.ProductSearchCondition;
import com.allra.market.domain.product.repository.ProductRepository;
import com.allra.market.domain.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(ProductSearchCondition condition, Pageable pageable) {
        return productRepository.search(condition, pageable);
    }
}
