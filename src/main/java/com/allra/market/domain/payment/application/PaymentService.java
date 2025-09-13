package com.allra.market.domain.payment.application;

import com.allra.market.common.exception.NotFoundException;
import com.allra.market.domain.order.domain.Order;
import com.allra.market.domain.order.domain.repository.OrderRepository;
import com.allra.market.domain.payment.application.response.PaymentResultDto;
import com.allra.market.domain.payment.domain.Payment;
import com.allra.market.domain.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.allra.market.common.exception.enums.ErrorCode.PAYMENT_FAILED;


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
