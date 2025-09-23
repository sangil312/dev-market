package com.dev.market.domain.payment.application;

import com.dev.market.common.exception.NotFoundException;
import com.dev.market.domain.order.domain.Order;
import com.dev.market.domain.order.domain.repository.OrderRepository;
import com.dev.market.domain.payment.application.response.PaymentResultDto;
import com.dev.market.domain.payment.domain.Payment;
import com.dev.market.domain.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.market.common.exception.enums.ErrorCode.PAYMENT_FAILED;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    @Transactional
    public Payment createPaymentAndOrderStatusUpdate(Order createdOrder, PaymentResultDto paymentResult) {
        Payment payment = Payment.create(createdOrder, paymentResult);
        paymentRepository.save(payment);

        Order order = orderRepository.findById(createdOrder.getId())
                .orElseThrow(() -> new NotFoundException(PAYMENT_FAILED));

        order.updateStatus(paymentResult.success());

        return payment;
    }

}
