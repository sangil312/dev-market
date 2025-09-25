package com.dev.market.domain.product.application;

import com.dev.market.domain.product.application.request.ProductSearchServiceRequest;
import com.dev.market.domain.product.application.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> searchProducts(ProductSearchServiceRequest request, Pageable pageable);
}
