package com.dev.market.domain.product.infrastructure;

import com.dev.market.common.exception.dto.ExternalApiException;
import com.dev.market.domain.order.infrastructure.PaymentGatewayServiceImpl;
import com.dev.market.domain.payment.application.response.PaymentResultDto;
import com.dev.market.domain.product.infrastructure.response.PaymentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceImplTest {


    @DisplayName("외부 결제 API 호출 시 결제 성공 응답을 받으면 PaymentResultDto 를 응답한다.")
    @Test
    void externalPaymentApiCall() {
        Long orderId = 1L;
        Long totalPrice = 1000L;

        PaymentResponse mockResponse = new PaymentResponse("SUCCESS", "txn_123456", "Payment processed successfully");

        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClient mockWebClient = mock(WebClient.class);

        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PaymentResponse.class)).thenReturn(Mono.just(mockResponse));

        PaymentGatewayServiceImpl paymentGatewayServiceImpl = new PaymentGatewayServiceImpl(mockWebClient);

        // when
        PaymentResultDto result = paymentGatewayServiceImpl.externalPaymentApiCall(orderId, totalPrice);

        // then
        assertThat(result).isNotNull();
        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(result.totalPrice()).isEqualTo(1000L);
        assertThat(result.success()).isEqualTo(true);
        assertThat(result.transactionId()).isEqualTo("txn_123456");
        assertThat(result.message()).isEqualTo("Payment processed successfully");
    }

    @DisplayName("외부 결제 API 호출 시 결제 실패 응답을 받으면 예외가 발생한다.")
    @Test
    void externalPaymentApiCallFailed() {
        Long orderId = 1L;
        Long totalPrice = 1000L;

        PaymentResponse mockResponse = new PaymentResponse("FAILED", null, "Something wrong!");

        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        WebClient mockWebClient = mock(WebClient.class);

        when(mockWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PaymentResponse.class)).thenReturn(Mono.just(mockResponse));

        PaymentGatewayServiceImpl paymentGatewayServiceImpl = new PaymentGatewayServiceImpl(mockWebClient);

        // when // then
        assertThatThrownBy(() ->  paymentGatewayServiceImpl.externalPaymentApiCall(orderId, totalPrice))
                .isInstanceOf(ExternalApiException.class);
    }
}