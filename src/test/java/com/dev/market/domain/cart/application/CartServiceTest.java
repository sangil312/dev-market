package com.dev.market.domain.cart.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.dev.market.IntegrationTestSupport;
import com.dev.market.common.exception.NotFoundException;
import com.dev.market.common.exception.QuantityOverException;
import com.dev.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.dev.market.domain.cart.application.request.CartItemDeleteServiceRequest;
import com.dev.market.domain.cart.application.request.CartItemUpdateServiceRequest;
import com.dev.market.domain.cart.application.response.CartAddResponse;
import com.dev.market.domain.cart.application.response.CartItemResponse;
import com.dev.market.domain.cart.application.response.CartResponse;
import com.dev.market.domain.cart.domain.Cart;
import com.dev.market.domain.cart.domain.repository.CartRepository;
import com.dev.market.domain.category.domain.Category;
import com.dev.market.domain.category.repository.CategoryRepository;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.product.domain.repository.ProductRepository;
import com.dev.market.domain.user.domain.User;
import com.dev.market.domain.user.domain.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CartServiceTest extends IntegrationTestSupport {

    @Autowired
    private CartService cartService;

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

    @Test
    @DisplayName("장바구니 상품 목록을 조회한다.")
    void findCart() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 1000L, 5);
        productRepository.saveAll(List.of(product1, product2));

        Cart cart = cartRepository.save(Cart.create(user));

        CartItemAddServiceRequest cartItem1 = new CartItemAddServiceRequest(product1.getId(), 1);
        CartItemAddServiceRequest cartItem2 = new CartItemAddServiceRequest(product2.getId(), 6);
        cart.addCartItem(product1, cartItem1);
        cart.addCartItem(product2, cartItem2);

        // when
        CartResponse response = cartService.findCart(user.getId());

        // then
        assertThat(response.cartId()).isEqualTo(cart.getId());
        assertThat(response.totalItemsQuantity()).isEqualTo(2);
        assertThat(response.totalPrice()).isEqualTo(7000L);
        assertThat(response.items()).hasSize(2)
                .extracting("productName", "quantity", "unitPrice", "subTotalPrice",
                        "isSoldOut", "quantityOver")
                .containsExactlyInAnyOrder(
                        tuple("상품1", 1, 1000L, 1000L, false, false),
                        tuple("상품2", 6, 1000L, 6000L, false, true)
                );
    }

    @Test
    @DisplayName("장바구니 상품 목록 조회 시 존재하지않는 사용자일 경우 예외가 발생한다.")
    void findCartUserNotFound() {
        //given //when // then
        assertThatThrownBy(() -> cartService.findCart(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("장바구니 상품 목록 조회 시 장바구니가 없을 경우 생성한다.")
    void findCartNotExistCart() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        // when
        CartResponse response = cartService.findCart(user.getId());

        // then
        assertThat(response.cartId()).isNotNull();
        assertThat(response.totalItemsQuantity()).isEqualTo(0);
        assertThat(response.totalPrice()).isEqualTo(0L);
        assertThat(response.items()).isEmpty();
    }

    @Test
    @DisplayName("장바구니에 상품 추가 시 해당 상품이 없으면 추가한다.")
    void addCartItem() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product1 = Product.create(category, "상품1", 1000L, 10);
        Product product2 = Product.create(category, "상품2", 1000L, 5);
        productRepository.saveAll(List.of(product1, product2));

        Cart cart = cartRepository.save(Cart.create(user));
        CartItemAddServiceRequest cartItem1 = new CartItemAddServiceRequest(product1.getId(), 1);
        cart.addCartItem(product1, cartItem1);

        CartItemAddServiceRequest request = new CartItemAddServiceRequest(product2.getId(), 1);

        // when
        CartAddResponse response = cartService.addCartItem(user.getId(), request);

        // then
        Integer savedCartItemQuantity = cart.getCartItems().get(0).getQuantity();
        assertThat(response.cartId()).isNotNull();
        assertThat(response.cartBadgeCount()).isEqualTo(2);
        assertThat(savedCartItemQuantity).isEqualTo(1);
    }

    @Test
    @DisplayName("장바구니에 상품 추가 시 해당 상품이 있으면 요청된 수량으로 변경한다.")
    void addCartItemDuplicateItem() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product = Product.create(category, "상품1", 1000L, 10);
        productRepository.save(product);

        Cart cart = cartRepository.save(Cart.create(user));
        CartItemAddServiceRequest cartItem = new CartItemAddServiceRequest(product.getId(), 5);
        cart.addCartItem(product, cartItem);

        CartItemAddServiceRequest request = new CartItemAddServiceRequest(product.getId(), 1);

        // when
        CartAddResponse response = cartService.addCartItem(user.getId(), request);

        // then
        Integer savedCartItemQuantity = cart.getCartItems().get(0).getQuantity();
        assertThat(response.cartId()).isNotNull();
        assertThat(response.cartBadgeCount()).isEqualTo(1);
        assertThat(savedCartItemQuantity).isEqualTo(1);
    }

    @Test
    @DisplayName("장바구니에 상품 추가 시 해당 상품이 존재하지 않는 상품이면 에외를 응답한다.")
    void addCartItemProductNotFound() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        cartRepository.save(Cart.create(user));

        CartItemAddServiceRequest request = new CartItemAddServiceRequest(9999L, 1);

        // when // then
        assertThatThrownBy(() -> cartService.addCartItem(user.getId(), request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("장바구니에 상품 추가 시 상품 재고보다 많은 수량으로 추가 시 예외가 발생한다.")
    void addCartItemQuantityOver() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product = Product.create(category, "상품1", 1000L, 1);
        productRepository.save(product);

        CartItemAddServiceRequest request = new CartItemAddServiceRequest(product.getId(), 2);

        // when // then
        assertThatThrownBy(() -> cartService.addCartItem(user.getId(), request))
                .isInstanceOf(QuantityOverException.class);
    }

    @Test
    @DisplayName("장바구니에 상품 수정(수량) 시 요청 수량으로 변경된다.")
    void updateCartItem() {
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

        em.flush();

        CartItemUpdateServiceRequest request = new CartItemUpdateServiceRequest(5);

        Long savedCartItemId = cart.getCartItems().get(0).getId();

        // when
        CartItemResponse response = cartService.updateCartItem(
                user.getId(),
                cart.getId(),
                savedCartItemId,
                request);

        // then
        assertThat(response).extracting(
                CartItemResponse::quantity,
                CartItemResponse::quantityOver,
                CartItemResponse::subTotalPrice
        ).containsExactly(5, false, 5000L);
    }

    @Test
    @DisplayName("장바구니 상품 수정(수량) 시 요청 상품이 존재하지 않으면 예외가 발생한다.")
    void updateCartItemNotFound() {
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

        CartItemUpdateServiceRequest request = new CartItemUpdateServiceRequest(1);

        Long notExistCartId = 99L;
        Long notExistCartItemId = 99L;

        // when // then
        assertThatThrownBy(() -> cartService.updateCartItem(user.getId(), notExistCartId, notExistCartItemId, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("장바구니 상품 수정(수량) 시 요청 수량이 존재하는 상품보다 많으면 예외가 발생한다.")
    void updateCartItemQuantityOver() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product = Product.create(category, "상품1", 1000L, 1);
        productRepository.save(product);

        Cart cart = cartRepository.save(Cart.create(user));
        CartItemAddServiceRequest cartItem = new CartItemAddServiceRequest(product.getId(), 1);
        cart.addCartItem(product, cartItem);

        em.flush();

        Long savedCartItemId = cart.getCartItems().get(0).getId();

        CartItemUpdateServiceRequest request = new CartItemUpdateServiceRequest(2);

        // when // then
        assertThatThrownBy(() -> cartService.updateCartItem(user.getId(), cart.getId(), savedCartItemId, request))
                .isInstanceOf(QuantityOverException.class);
    }

    @Test
    @DisplayName("장바구니에서 선택된 상품을 장바구니에서 삭제한다.")
    void deleteCartItem() {
        // given
        User user = User.create("user1");
        userRepository.save(user);

        Category category = Category.create("카테고리1");
        categoryRepository.save(category);

        Product product = Product.create(category, "상품1", 1000L, 1);
        productRepository.save(product);

        Cart cart = cartRepository.save(Cart.create(user));
        CartItemAddServiceRequest cartItem = new CartItemAddServiceRequest(product.getId(), 1);
        cart.addCartItem(product, cartItem);

        em.flush();

        Long savedCartItemId = cart.getCartItems().get(0).getId();

        CartItemDeleteServiceRequest request = new CartItemDeleteServiceRequest(List.of(savedCartItemId));

        // when
        cartService.deleteCartItem(user.getId(), cart.getId(), request);

        // then
        em.flush();
        em.clear();
        Cart after = cartRepository.findById(cart.getId()).orElseThrow();
        assertThat(after.getCartItems()).isEmpty();
    }
}