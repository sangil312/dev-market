package com.allra.market.domain.order.interfaces;

import com.allra.market.domain.order.application.OrderAndPayFacadeService;
import com.allra.market.domain.order.application.response.OrderResponse;
import com.allra.market.domain.order.interfaces.request.OrderCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Validated
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderAndPayFacadeService orderAndPayFacadeService;

    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> createOrderAndPay(
            @NotBlank(message = "멱등성 키는 필수입니다.") @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        return ResponseEntity.ok(orderAndPayFacadeService.createOrderAndPay(
                idempotencyKey,
                1L,
                request.toServiceRequest(),
                LocalDateTime.now()));
    }
}
