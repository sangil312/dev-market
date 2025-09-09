package com.allra.market.api.service;

import static org.assertj.core.api.Assertions.*;

import com.allra.market.api.IntegrationTestSupport;
import com.allra.market.domain.product.request.ProductSearchCondition;
import com.allra.market.domain.product.response.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("상품 조회 시 검색 조건 없이 요청 사이즈로 조회한다.")
    void searchProductsWithNoneCondition() {
        // given
        ProductSearchCondition condition = new ProductSearchCondition(null, null, null, null);
        PageRequest pageable = PageRequest.of(0, 5);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(5)
                .allSatisfy(productResponse -> {
                    assertThat(productResponse.productId()).isNotNull();
                    assertThat(productResponse.categoryId()).isNotNull();
                    assertThat(productResponse.productName()).isNotNull();
                    assertThat(productResponse.price()).isNotNull();
                    assertThat(productResponse.productQuantity()).isNotNull();
                    assertThat(productResponse.isSoldOut()).isNotNull();
                });
    }

    @Test
    @DisplayName("상품 조회 시 모든 조건으로 조회한다.")
    void searchProductsWhitCondition() {
        // given
        ProductSearchCondition condition = new ProductSearchCondition(2L, "아이폰", 0L, 2_000_000L);
        PageRequest pageable = PageRequest.of(0, 20);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(1)
                .extracting("productId", "categoryId", "productName", "price", "productQuantity", "isSoldOut")
                .containsExactlyInAnyOrder(tuple(3L, 2L, "아이폰", 1_900_000L, 0, true));
    }

    @Test
    @DisplayName("상품 조회 시 카테고리 ID 로만 조회한다.")
    void searchProductsWhitCategoryId() {
        // given
        ProductSearchCondition condition = new ProductSearchCondition(1L, null, null, null);
        PageRequest pageable = PageRequest.of(0, 20);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(2)
                .extracting("productId", "categoryId", "productName", "price", "productQuantity", "isSoldOut")
                .containsExactlyInAnyOrder(
                        tuple(1L, 1L, "나이키", 150_000L, 0, true),
                        tuple(2L, 1L, "아디다스", 200_000L, 10, false)
                );
    }

    @Test
    @DisplayName("상품 조회 시 상품명으로만 조회한다.")
    void searchProductsWhitProductName() {
        // given
        ProductSearchCondition condition = new ProductSearchCondition(null, "폰", null, null);
        PageRequest pageable = PageRequest.of(0, 20);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(1)
                .extracting("productId", "categoryId", "productName", "price", "productQuantity", "isSoldOut")
                .containsExactlyInAnyOrder(tuple(3L, 2L, "아이폰", 1900_000L, 0, true));
    }

    @Test
    @DisplayName("상품 조회 시 최소 가격 이상 최대 가격 이하로 조회한다.")
    void searchProductsWhitBetweenPrice() {
        // given
        ProductSearchCondition condition = new ProductSearchCondition(null, null, 150_000L, 1_900_000L);
        PageRequest pageable = PageRequest.of(0, 20);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(3)
                .extracting("productId", "categoryId", "productName", "price", "productQuantity", "isSoldOut")
                .containsExactlyInAnyOrder(
                        tuple(1L, 1L, "나이키", 150_000L, 0, true),
                        tuple(2L, 1L, "아디다스", 200_000L, 10, false),
                        tuple(3L, 2L, "아이폰", 1_900_000L, 0, true)
                );
    }
}