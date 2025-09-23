package com.dev.market.domain.payment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dev.market.IntegrationTestSupport;
import com.dev.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.dev.market.domain.cart.domain.Cart;
import com.dev.market.domain.cart.domain.repository.CartRepository;
import com.dev.market.domain.category.domain.Category;
import com.dev.market.domain.category.repository.CategoryRepository;
import com.dev.market.domain.order.domain.Order;
import com.dev.market.domain.order.domain.enums.OrderStatus;
import com.dev.market.domain.order.domain.repository.OrderRepository;
import com.dev.market.domain.payment.application.response.PaymentResultDto;
import com.dev.market.domain.payment.domain.Payment;
import com.dev.market.domain.payment.domain.PaymentStatus;
import com.dev.market.domain.payment.domain.repository.PaymentRepository;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.product.domain.repository.ProductRepository;
import com.dev.market.domain.user.domain.User;
import com.dev.market.domain.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class PaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("결제 성공시 결제 성공 내역을 추가하고 주문 상태를 결제 완료로 변경한다.")
    void createPaymentAndOrderStatusUpdateWithSuccess() {
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

        Order order = Order.create("idempotencyKey", user, cart.getCartItems(), LocalDateTime.now());
        orderRepository.save(order);

        PaymentResultDto request = PaymentResultDto.builder()
                .orderId(order.getId())
                .totalPrice(1000L)
                .success(true)
                .transactionId("txn_123")
                .message(null)
                .build();

        // when
        paymentService.createPaymentAndOrderStatusUpdate(order, request);

        // then
        Order result = orderRepository.findById(order.getId()).get();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        Payment payment = paymentRepository.findByOrderId(order.getId()).get();
        assertThat(payment).extracting("amount", "status", "transactionId")
                .containsExactlyInAnyOrder(1000L, PaymentStatus.SUCCESS, "txn_123");
    }

    @Test
    @DisplayName("결제 실패시 결제 실패 내역을 추가하고 주문 상태를 결제 실패로 변경한다.")
    void createPaymentAndOrderStatusUpdateWithFail() {
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

        Order order = Order.create("idempotencyKey", user, cart.getCartItems(), LocalDateTime.now());
        orderRepository.save(order);

        PaymentResultDto request = PaymentResultDto.builder()
                .orderId(order.getId())
                .totalPrice(1000L)
                .success(false)
                .transactionId(null)
                .message(null)
                .build();

        // when
        paymentService.createPaymentAndOrderStatusUpdate(order, request);

        // then
        Order result = orderRepository.findById(order.getId()).get();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        Payment payment = paymentRepository.findByOrderId(order.getId()).get();
        assertThat(payment).extracting("amount", "status", "transactionId")
                .containsExactlyInAnyOrder(1000L, PaymentStatus.FAILED, null);
    }
}