package com.dev.market.domain.product.interfaces;

import com.dev.market.domain.product.application.ProductServiceImpl;
import com.dev.market.domain.product.interfaces.request.ProductSearchRequest;
import com.dev.market.domain.product.application.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productServiceImpl;

    @GetMapping("/api/products")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            ProductSearchRequest condition,
            Pageable pageable
    ) {
        return ResponseEntity.ok(productServiceImpl.searchProducts(condition.toServiceRequest(), pageable));
    }
}
