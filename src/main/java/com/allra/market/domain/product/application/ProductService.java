package com.allra.market.domain.product.application;

import com.allra.market.domain.product.application.request.ProductSearchCondition;
import com.allra.market.domain.product.domain.repository.ProductRepository;
import com.allra.market.domain.product.interfaces.response.ProductResponse;
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
