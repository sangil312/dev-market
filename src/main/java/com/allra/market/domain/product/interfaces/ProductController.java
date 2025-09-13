package com.allra.market.domain.product.interfaces;

import com.allra.market.domain.product.application.ProductService;
import com.allra.market.domain.product.application.request.ProductSearchCondition;
import com.allra.market.domain.product.application.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/products")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            ProductSearchCondition condition,
            Pageable pageable
    ) {
        return ResponseEntity.ok(productService.searchProducts(condition, pageable));
    }
}
