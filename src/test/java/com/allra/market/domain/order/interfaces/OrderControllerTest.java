package com.allra.market.domain.order.interfaces;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.allra.market.ControllerTestSupport;
import com.allra.market.domain.order.application.response.OrderResponse;
import com.allra.market.domain.order.interfaces.request.OrderCreateRequest;
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

        when(orderAndPayFacadeService.createOrderAndPay(anyLong(), any(), any()))
                .thenReturn(response);

        //when //then
        mockMvc.perform(
                post("/api/orders")
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

        when(orderAndPayFacadeService.createOrderAndPay(anyLong(), any(), any()))
                .thenReturn(response);

        //when //then
        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.paymentId").value(1L))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("결제에 실패했습니다."));
    }
}