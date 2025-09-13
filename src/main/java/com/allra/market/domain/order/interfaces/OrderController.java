package com.allra.market.domain.order.interfaces;

import com.allra.market.domain.order.application.OrderAndPayFacadeService;
import com.allra.market.domain.order.application.response.OrderResponse;
import com.allra.market.domain.order.interfaces.request.OrderCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderAndPayFacadeService orderAndPayFacadeService;

    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> createOrderAndPay(@Valid @RequestBody OrderCreateRequest request) {

        return ResponseEntity.ok(
                orderAndPayFacadeService.createOrderAndPay(1L, request.toServiceRequest(),
                        LocalDateTime.now()));
    }
}
