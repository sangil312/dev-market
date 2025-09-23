package com.dev.market.domain.order.interfaces;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.market.ControllerTestSupport;
import com.dev.market.domain.order.application.response.OrderResponse;
import com.dev.market.domain.order.interfaces.request.OrderCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

class OrderControllerTest extends ControllerTestSupport {

    @DisplayName("주문에 성공하면 성공 응답을 한다.")
    @Test
    void createOrderAndPay() throws Exception {
        //given
        OrderCreateRequest request = new OrderCreateRequest(1L, List.of(1L, 2L));

        OrderResponse response = OrderResponse.of(1L, 1L, true);

        when(orderAndPayFacadeService.createOrderAndPay(anyString(), anyLong(), any(), any()))
                .thenReturn(response);

        //when //then
        mockMvc.perform(
                        post("/api/orders")
                                .header("Idempotency-Key", "idempotencyKey")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.paymentId").value(1L))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("결제에 성공했습니다."));
    }

    @DisplayName("주문에 실패하면 실패 응답을 한다.")
    @Test
    void createOrderAndPayWithFailed() throws Exception {
        //given
        OrderCreateRequest request = new OrderCreateRequest(1L, List.of(1L, 2L));

        OrderResponse response = OrderResponse.of(1L, 1L, false);

        when(orderAndPayFacadeService.createOrderAndPay(anyString(), anyLong(), any(), any()))
                .thenReturn(response);

        //when //then
        mockMvc.perform(
                        post("/api/orders")
                                .header("Idempotency-Key", "idempotencyKey")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.paymentId").value(1L))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("결제에 실패했습니다."));
    }

    @DisplayName("주문 요청 시 장바구니 ID 값이 없으면 오류메세지를 응답한다.")
    @Test
    void createOrderAndPayWithCartItemsEmpty() throws Exception {
        //given
        OrderCreateRequest request = new OrderCreateRequest(1L, null);

        OrderResponse response = OrderResponse.of(1L, 1L, false);

        when(orderAndPayFacadeService.createOrderAndPay(anyString(), anyLong(), any(), any()))
                .thenReturn(response);

        //when //then
        mockMvc.perform(
                        post("/api/orders")
                                .header("Idempotency-Key", "idempotencyKey")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("장바구니에 상품을 선택해주세요."));
    }

    @DisplayName("주문 요청 시 멱등성 키가 없으면 오류 메세지를 응답한다.")
    @Test
    void createOrderAndPayWithIdempotencyKeyEmpty() throws Exception {
        //given
        OrderCreateRequest request = new OrderCreateRequest(1L, List.of(1L, 2L));

        OrderResponse response = OrderResponse.of(1L, 1L, false);

        when(orderAndPayFacadeService.createOrderAndPay(anyString(), anyLong(), any(), any()))
                .thenReturn(response);

        //when //then
        mockMvc.perform(
                        post("/api/orders")
                                .header("Idempotency-Key", "")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("멱등성 키는 필수입니다."));
    }
}