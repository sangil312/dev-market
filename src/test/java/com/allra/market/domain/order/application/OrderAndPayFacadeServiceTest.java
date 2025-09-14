package com.allra.market.domain.order.application;


import com.allra.market.IntegrationTestSupport;
import com.allra.market.common.exception.dto.ExternalApiException;
import com.allra.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.allra.market.domain.cart.domain.Cart;
import com.allra.market.domain.cart.domain.CartItem;
import com.allra.market.domain.cart.domain.repository.CartRepository;
import com.allra.market.domain.category.domain.Category;
import com.allra.market.domain.category.repository.CategoryRepository;
import com.allra.market.domain.order.application.repuest.OrderCreateServiceRequest;
import com.allra.market.domain.order.application.response.OrderResponse;
import com.allra.market.domain.order.domain.Order;
import com.allra.market.domain.order.domain.enums.OrderStatus;
import com.allra.market.domain.order.domain.repository.OrderRepository;
import com.allra.market.domain.payment.application.response.PaymentResultDto;
import com.allra.market.domain.payment.domain.Payment;
import com.allra.market.domain.payment.domain.PaymentStatus;
import com.allra.market.domain.payment.domain.repository.PaymentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Transactional
class OrderAndPayFacadeServiceTest extends IntegrationTestSupport {

    @Autowired
    private OrderAndPayFacadeService orderAndPayFacadeService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
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

    @DisplayName("주문을 생성하고 결제 api 성공 시 상품 재고 차감, 결제 성공 이력 추가와 주문 상태를 변경한다.")
    @Test
    void createOrderAndPay() {
        //given
        String idempotencyKey = "idempotencyKey";
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
        Long cartItemId = cart.getCartItems().get(0).getId();
        OrderCreateServiceRequest request = new OrderCreateServiceRequest(cart.getId(), List.of(cartItemId));

        when(paymentApiService.externalPaymentApiCall(anyLong(), any()))
                .thenReturn(new PaymentResultDto(
                        1L, 1000L, true, "txn_123456", null));

        //when
        OrderResponse response = orderAndPayFacadeService.createOrderAndPay(idempotencyKey, user.getId(), request, LocalDateTime.now());

        //then
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("결제에 성공했습니다.");

        // 재고차감
        Product result = productRepository.findById(product.getId()).get();
        assertThat(result.getQuantity()).isEqualTo(9);
        // 장바구니 상품 삭제
        List<CartItem> cartItems = cartRepository.findCartItemsByCartId(cart.getId());
        assertThat(cartItems).isEmpty();
        // 주문 상태 변경
        Order order = orderRepository.findById(response.orderId()).get();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        // 결제 이력 추가
        Payment payment = paymentRepository.findById(response.paymentId()).get();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getTransactionId()).isEqualTo("txn_123456");
    }

    @DisplayName("주문을 생성하고 결제 api 실패 시 상품 재고 롤백, 결제 실패 이력 추가와 주문 상태를 변경한다.")
    @Test
    void createOrderAndPayWithPaymentApiFailed() {
        //given
        String idempotencyKey = "idempotencyKey";

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

        Long cartItemId = cart.getCartItems().get(0).getId();
        OrderCreateServiceRequest request = new OrderCreateServiceRequest(cart.getId(), List.of(cartItemId));

        when(paymentApiService.externalPaymentApiCall(anyLong(), any()))
                .thenThrow(ExternalApiException.class);

        //when
        OrderResponse response = orderAndPayFacadeService.createOrderAndPay(idempotencyKey, user.getId(), request, LocalDateTime.now());

        //then
        assertThat(response).isNotNull();
        assertThat(response.message()).isEqualTo("결제에 실패했습니다.");

        // 재고차감
        Product result = productRepository.findById(product.getId()).get();
        assertThat(result.getQuantity()).isEqualTo(10);
        // 장바구니 상품 삭제
        List<CartItem> cartItems = cartRepository.findCartItemsByCartId(cart.getId());
        assertThat(cartItems).isNotEmpty();
        // 주문 상태 변경
        Order order = orderRepository.findById(response.orderId()).get();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        // 결제 이력 추가, 트랜잭션 ID 추가
        Payment payment = paymentRepository.findById(response.paymentId()).get();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

}