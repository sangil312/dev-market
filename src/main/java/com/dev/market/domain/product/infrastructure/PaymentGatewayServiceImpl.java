package com.dev.market.domain.product.infrastructure;

import com.dev.market.common.exception.dto.ExternalApiException;
import com.dev.market.domain.order.application.PaymentGatewayService;
import com.dev.market.domain.payment.application.response.PaymentResultDto;
import com.dev.market.domain.product.infrastructure.request.PaymentRequest;
import com.dev.market.domain.product.infrastructure.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

import static com.dev.market.common.exception.enums.ErrorCode.PAYMENT_FAILED;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private final WebClient webClient;

    @Override
    public PaymentResultDto externalPaymentApiCall(Long orderId, Long totalPrice) {
        PaymentRequest request = new PaymentRequest(orderId.toString(), totalPrice);

        try {
            PaymentResponse response = webClient.post()
                    .uri("https://devmarket.free.beeceptor.com/api/v1/payment")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .block();

            if (!Objects.isNull(response) && response.status().equalsIgnoreCase("SUCCESS")) {
                return response.toDto(orderId, totalPrice);
            }
            throw new ExternalApiException(PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("외부 결제 API 호출 실패 - orderId: {}", orderId, e);
            throw new ExternalApiException(PAYMENT_FAILED);
        }
    }
}
