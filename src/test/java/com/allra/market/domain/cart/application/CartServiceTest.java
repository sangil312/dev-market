package com.allra.market.domain.cart.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.allra.market.IntegrationTestSupport;
import com.allra.market.common.exception.NotFoundException;
import com.allra.market.common.exception.QuantityOverException;
import com.allra.market.domain.cart.application.dto.request.AddCartItemRequest;
import com.allra.market.domain.cart.application.dto.request.DeleteCartItemRequest;
import com.allra.market.domain.cart.application.dto.request.UpdateCartItemRequest;
import com.allra.market.domain.cart.application.dto.response.CartAddResponse;
import com.allra.market.domain.cart.application.dto.response.CartItemResponse;
import com.allra.market.domain.cart.application.dto.response.CartResponse;
import com.allra.market.domain.cart.domain.Cart;
import com.allra.market.domain.cart.domain.repository.CartRepository;
import com.allra.market.domain.category.domain.Category;
import com.allra.market.domain.category.repository.CategoryRepository;
import com.allra.market.domain.product.domain.Product;
import com.allra.market.domain.product.domain.repository.ProductRepository;
import com.allra.market.domain.user.domain.User;
import com.allra.market.domain.user.domain.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CartServiceTest extends IntegrationTestSupport {

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EntityManager em;

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

        AddCartItemRequest cartItem1 = new AddCartItemRequest(product1.getId(), 1);
        AddCartItemRequest cartItem2 = new AddCartItemRequest(product2.getId(), 6);
        cart.addCartItem(product1, cartItem1);
        cart.addCartItem(product2, cartItem2);

        // when
        CartResponse response = cartService.findCart(user.getId());

        // then
        assertThat(response.cartId()).isNotNull();
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
        AddCartItemRequest cartItem1 = new AddCartItemRequest(product1.getId(), 1);
        cart.addCartItem(product1, cartItem1);

        AddCartItemRequest request = new AddCartItemRequest(product2.getId(), 1);

        // when
        CartAddResponse response = cartService.addCartItem(user.getId(), request);

        // then
        Integer savedCartItemQuantity = cart.getCartItemList().get(0).getQuantity();
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
        AddCartItemRequest cartItem = new AddCartItemRequest(product.getId(), 5);
        cart.addCartItem(product, cartItem);

        AddCartItemRequest request = new AddCartItemRequest(product.getId(), 1);

        // when
        CartAddResponse response = cartService.addCartItem(user.getId(), request);

        // then
        Integer savedCartItemQuantity = cart.getCartItemList().get(0).getQuantity();
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

        AddCartItemRequest request = new AddCartItemRequest(9999L, 1);

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

        AddCartItemRequest request = new AddCartItemRequest(product.getId(), 2);

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
        AddCartItemRequest cartItem = new AddCartItemRequest(product.getId(), 1);
        cart.addCartItem(product, cartItem);

        em.flush();

        UpdateCartItemRequest request = new UpdateCartItemRequest(5);

        Long savedCartItemId = cart.getCartItemList().get(0).getId();

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
        AddCartItemRequest cartItem = new AddCartItemRequest(product.getId(), 1);
        cart.addCartItem(product, cartItem);

        UpdateCartItemRequest request = new UpdateCartItemRequest(1);

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
        AddCartItemRequest cartItem = new AddCartItemRequest(product.getId(), 1);
        cart.addCartItem(product, cartItem);

        em.flush();

        Long savedCartItemId = cart.getCartItemList().get(0).getId();

        UpdateCartItemRequest request = new UpdateCartItemRequest(2);

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
        AddCartItemRequest cartItem = new AddCartItemRequest(product.getId(), 1);
        cart.addCartItem(product, cartItem);

        em.flush();

        Long savedCartItemId = cart.getCartItemList().get(0).getId();

        DeleteCartItemRequest request = new DeleteCartItemRequest(List.of(savedCartItemId));

        // when
        cartService.deleteCartItem(user.getId(), cart.getId(), request);

        // then
        em.flush();
        em.clear();
        Cart after = cartRepository.findById(cart.getId()).orElseThrow();
        assertThat(after.getCartItemList()).isEmpty();
    }
}