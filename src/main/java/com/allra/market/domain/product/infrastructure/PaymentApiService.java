package com.allra.market.domain.product.infrastructure;

import com.allra.market.common.exception.dto.ExternalApiException;
import com.allra.market.domain.payment.application.response.PaymentResultDto;
import com.allra.market.domain.product.infrastructure.request.PaymentRequest;
import com.allra.market.domain.product.infrastructure.response.PaymentResponse;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Objects;

import static com.allra.market.common.exception.enums.ErrorCode.PAYMENT_FAILED;


@Slf4j
@Service
public class PaymentApiService {

    public PaymentResultDto externalPaymentApiCall(Long orderId, Long totalPrice) {
        PaymentRequest request = new PaymentRequest(orderId.toString(), totalPrice);

        try {
            PaymentResponse response = webClient().post()
                    .uri("https://allramarket.free.beeceptor.com/api/v1/payment")
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

    private WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
