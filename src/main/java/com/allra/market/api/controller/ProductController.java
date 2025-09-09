package com.allra.market.api.controller;

import com.allra.market.api.service.ProductService;
import com.allra.market.domain.product.request.ProductSearchCondition;
import com.allra.market.domain.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public Page<ProductResponse> searchProducts(ProductSearchCondition condition, Pageable pageable) {
        return productService.searchProducts(condition, pageable);
    }
}
