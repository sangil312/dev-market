package com.allra.market.domain.payment.domain;

import com.allra.market.common.entity.BaseCreatedAtEntity;
import com.allra.market.domain.order.domain.Order;
import com.allra.market.domain.payment.application.response.PaymentResultDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String transactionId;

    public static Payment create(final Order order, final PaymentResultDto result) {
        Payment payment = new Payment();
        payment.order = order;
        payment.amount = result.totalPrice();
        payment.status = result.success() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        payment.transactionId = result.transactionId() == null ? "N/A" : result.transactionId();
        return payment;
    }
}
