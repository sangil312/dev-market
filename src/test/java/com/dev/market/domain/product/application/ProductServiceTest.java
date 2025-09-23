package com.dev.market.domain.product.application;

import static org.assertj.core.api.Assertions.*;

import com.dev.market.IntegrationTestSupport;
import com.dev.market.common.exception.QuantityOverException;
import com.dev.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.dev.market.domain.cart.domain.Cart;
import com.dev.market.domain.cart.domain.repository.CartRepository;
import com.dev.market.domain.category.domain.Category;
import com.dev.market.domain.category.repository.CategoryRepository;
import com.dev.market.domain.order.domain.Order;
import com.dev.market.domain.product.application.request.ProductSearchCondition;
import com.dev.market.domain.product.application.response.ProductResponse;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.product.domain.repository.ProductRepository;
import com.dev.market.domain.user.domain.User;
import com.dev.market.domain.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("상품 조회 시 검색 조건 없이 요청 사이즈로 조회한다.")
    void searchProductsWithNoneCondition() {
        // given
        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 2000L, 0);
        productRepository.saveAll(List.of(product1, product2));

        ProductSearchCondition condition = new ProductSearchCondition(null, null, null, null);
        PageRequest pageable = PageRequest.of(0, 5);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses.getContent()).hasSize(2)
                .extracting(
                        ProductResponse::productName,
                        ProductResponse::price,
                        ProductResponse::productQuantity,
                        ProductResponse::isSoldOut
                )
                .containsExactlyInAnyOrder(
                        tuple("상품1", 1000L, 10, false),
                        tuple("상품2", 2000L, 0, true)
                );
    }

    @Test
    @DisplayName("상품 조회 시 모든 조건으로 조회한다.")
    void searchProductsWhitCondition() {
        // given
        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 2000L, 0);
        productRepository.saveAll(List.of(product1, product2));

        ProductSearchCondition condition = new ProductSearchCondition(category.getId(), "상품1", 100L, 1000L);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(1)
                .extracting("productName", "price", "productQuantity", "isSoldOut")
                .containsExactlyInAnyOrder(tuple("상품1", 1000L, 10, false));
    }

    @Test
    @DisplayName("상품 조회 시 카테고리 ID 로만 조회한다.")
    void searchProductsWhitCategoryId() {
        // given
        Category category1 = Category.create("카테고리1");
        Category category2 = Category.create("카테고리2");
        categoryRepository.saveAll(List.of(category1, category2));

        Product product1 = Product.create(category1, "상품1", 1000L, 10);
        Product product2 = Product.create(category2, "상품2", 2000L, 0);
        productRepository.saveAll(List.of(product1, product2));

        ProductSearchCondition condition = new ProductSearchCondition(category1.getId(), null, null, null);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(1)
                .extracting("productName", "price", "productQuantity", "isSoldOut")
                .containsExactlyInAnyOrder(tuple("상품1", 1000L, 10, false));
    }

    @Test
    @DisplayName("상품 조회 시 상품명으로만 조회한다.")
    void searchProductsWhitProductName() {
        // given
        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 2000L, 0);
        productRepository.saveAll(List.of(product1, product2));

        ProductSearchCondition condition = new ProductSearchCondition(null, "상품1", null, null);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(1)
                .extracting("productName", "price", "productQuantity", "isSoldOut")
                .containsExactlyInAnyOrder(tuple("상품1", 1000L, 10, false));
    }

    @Test
    @DisplayName("상품 조회 시 최소 가격 이상 최대 가격 이하로 조회한다.")
    void searchProductsWhitBetweenPrice() {
        // given
        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 2000L, 0);
        Product product3 = Product.create(category, "상품3", 3000L, 10);
        productRepository.saveAll(List.of(product1, product2, product3));

        ProductSearchCondition condition = new ProductSearchCondition(null, null, 2000L, 3000L);
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductResponse> productResponses = productService.searchProducts(condition, pageable);

        // then
        assertThat(productResponses).hasSize(2)
                .extracting("productName", "price", "productQuantity", "isSoldOut")
                .containsExactlyInAnyOrder(
                        tuple("상품2", 2000L, 0, true),
                        tuple("상품3", 3000L, 10, false)
                );
    }

    @Test
    @DisplayName("상품 재고를 차감시킨다.")
    void productDecreasesStock() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product = Product.create(category, "상품1", 1000L, 10);
        productRepository.save(product);

        Cart cart = cartRepository.save(Cart.create(user));
        CartItemAddServiceRequest cartItem = new CartItemAddServiceRequest(product.getId(), 1);
        cart.addCartItem(product, cartItem);

        // when
        productService.productDecreasesStock(cart.getCartItems());

        // then
        assertThat(product.getQuantity()).isEqualTo(9);
    }

    @Test
    @DisplayName("상품 재고를 차감 시 재고를 초과하면 예외가 발생한다.")
    void productDecreasesStockWithQuantityOver() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product = Product.create(category, "상품1", 1000L, 1);
        productRepository.save(product);

        Cart cart = cartRepository.save(Cart.create(user));
        CartItemAddServiceRequest cartItem = new CartItemAddServiceRequest(product.getId(), 2);
        cart.addCartItem(product, cartItem);

        // when // then
        assertThatThrownBy(() -> productService.productDecreasesStock(cart.getCartItems()))
                .isInstanceOf(QuantityOverException.class);
    }

    @Test
    @DisplayName("상품 재고를 롤백한다.")
    void productStockRollback() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product = Product.create(category, "상품1", 1000L, 0);
        productRepository.save(product);

        Cart cart = cartRepository.save(Cart.create(user));
        CartItemAddServiceRequest cartItem = new CartItemAddServiceRequest(product.getId(), 1);
        cart.addCartItem(product, cartItem);

        Order order = Order.create("idempotencyKey", user, cart.getCartItems(), LocalDateTime.now());

        // when
        productService.productStockRollback(order.getOrderItems());

        // then
        assertThat(product.getQuantity()).isEqualTo(1);
    }
}