package com.allra.market.domain.order.application;


import com.allra.market.IntegrationTestSupport;
import com.allra.market.common.exception.NotFoundException;
import com.allra.market.common.exception.QuantityOverException;
import com.allra.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.allra.market.domain.cart.domain.Cart;
import com.allra.market.domain.cart.domain.repository.CartRepository;
import com.allra.market.domain.category.domain.Category;
import com.allra.market.domain.category.repository.CategoryRepository;
import com.allra.market.domain.order.application.repuest.OrderCreateServiceRequest;
import com.allra.market.domain.order.application.response.OrderCreateResponse;
import com.allra.market.domain.order.domain.Order;
import com.allra.market.domain.order.domain.enums.OrderStatus;
import com.allra.market.domain.product.domain.Product;
import com.allra.market.domain.product.domain.repository.ProductRepository;
import com.allra.market.domain.user.domain.User;
import com.allra.market.domain.user.domain.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class OrderServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    @DisplayName("주문 생성 시 상품 재고를 차감한다.")
    @Test
    void createOrderAndProductStockDecreases() {
        //given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 1000L, 10);
        productRepository.saveAll(List.of(product1, product2));

        Cart cart = cartRepository.save(Cart.create(user));

        CartItemAddServiceRequest cartItem1 = new CartItemAddServiceRequest(product1.getId(), 1);
        CartItemAddServiceRequest cartItem2 = new CartItemAddServiceRequest(product2.getId(), 1);
        cart.addCartItem(product1, cartItem1);
        cart.addCartItem(product2, cartItem2);

        em.flush();

        List<Long> cartItemIds = List.of(
                cart.getCartItems().get(0).getId(),
                cart.getCartItems().get(1).getId());

        OrderCreateServiceRequest request = new OrderCreateServiceRequest(cart.getId(), cartItemIds);

        //when
        OrderCreateResponse orderCreateResponse = orderService.createOrderAndProductStockDecreases(user.getId(), request, LocalDateTime.now());
        Order response = orderCreateResponse.order();

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTotalPrice()).isEqualTo(2000L);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.getOrderItems()).hasSize(2)
                .extracting("order", "product", "quantity", "unitPrice")
                .containsExactlyInAnyOrder(
                        tuple(response, product1,  1, 1000L),
                        tuple(response, product2,  1, 1000L)
                );

        assertThat(product1.getQuantity()).isEqualTo(9);
        assertThat(product2.getQuantity()).isEqualTo(9);
    }

    @DisplayName("주문 생성 시 장바구니에 있는 수량보다 상품 수량이 적으면 예외가 발생한다.")
    @Test
    void createOrderAndProductStockDecreasesWithProductStockLess() {
        //given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 1000L, 10);
        productRepository.saveAll(List.of(product1, product2));

        Cart cart = cartRepository.save(Cart.create(user));

        CartItemAddServiceRequest cartItem1 = new CartItemAddServiceRequest(product1.getId(), 1);
        CartItemAddServiceRequest cartItem2 = new CartItemAddServiceRequest(product2.getId(), 11);
        cart.addCartItem(product1, cartItem1);
        cart.addCartItem(product2, cartItem2);

        em.flush();

        List<Long> cartItemIds = List.of(
                cart.getCartItems().get(0).getId(),
                cart.getCartItems().get(1).getId());

        OrderCreateServiceRequest request = new OrderCreateServiceRequest(cart.getId(), cartItemIds);

        //when //then
        assertThatThrownBy(() -> orderService.createOrderAndProductStockDecreases(user.getId(), request, LocalDateTime.now()))
                .isInstanceOf(QuantityOverException.class);
    }

    @DisplayName("주문 생성 시 장바구니에 있는 상품이 존재하지 않으면 예외가 발생한다.")
    @Test
    void createOrderAndProductStockDecreasesWithProductNotExist() {
        //given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 1000L, 10);
        productRepository.saveAll(List.of(product1, product2));

        Cart cart = cartRepository.save(Cart.create(user));

        CartItemAddServiceRequest cartItem1 = new CartItemAddServiceRequest(1L, 1);
        CartItemAddServiceRequest cartItem2 = new CartItemAddServiceRequest(2L, 1);
        cart.addCartItem(product1, cartItem1);
        cart.addCartItem(product2, cartItem2);

        OrderCreateServiceRequest request = new OrderCreateServiceRequest(cart.getId(), List.of(99L, 100L));

        //when //then
        assertThatThrownBy(() -> orderService.createOrderAndProductStockDecreases(1L, request, LocalDateTime.now()))
                .isInstanceOf(NotFoundException.class);
    }
}