package com.allra.market.domain.cart.interfaces;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.allra.market.ControllerTestSupport;
import com.allra.market.domain.cart.application.dto.request.AddCartItemRequest;
import com.allra.market.domain.cart.application.dto.request.DeleteCartItemRequest;
import com.allra.market.domain.cart.application.dto.request.UpdateCartItemRequest;
import com.allra.market.domain.cart.application.dto.response.CartAddResponse;
import com.allra.market.domain.cart.application.dto.response.CartItemResponse;
import com.allra.market.domain.cart.application.dto.response.CartResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CartControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("장바구니를 조회한다.")
    void findCart() throws Exception {
        // given
        CartItemResponse item = CartItemResponse.builder()
                .cartItemId(1L)
                .productId(1L)
                .productName("상품1")
                .quantity(1)
                .unitPrice(1000L)
                .subTotalPrice(1000L)
                .isSoldOut(false)
                .quantityOver(false)
                .build();

        CartResponse response = CartResponse.of(1L, List.of(item));

        when(cartService.findCart(anyLong())).thenReturn(response);

        // when // then
        mockMvc.perform(
                    get("/carts/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L))
                .andExpect(jsonPath("$.totalItemsQuantity").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(1000L))
                .andExpect(jsonPath("$.items[0].cartItemId").value(1L))
                .andExpect(jsonPath("$.items[0].productId").value(1L))
                .andExpect(jsonPath("$.items[0].productName").value("상품1"))
                .andExpect(jsonPath("$.items[0].quantity").value(1L))
                .andExpect(jsonPath("$.items[0].unitPrice").value(1000L))
                .andExpect(jsonPath("$.items[0].subTotalPrice").value(1000L))
                .andExpect(jsonPath("$.items[0].isSoldOut").value(false))
                .andExpect(jsonPath("$.items[0].quantityOver").value(false));
    }

    @Test
    @DisplayName("장바구니에 상품을 추가한다.")
    void addCartItem() throws Exception {
        // given
        AddCartItemRequest request = new AddCartItemRequest(1L, 1);

        CartAddResponse response = new CartAddResponse(1L, 1);

        when(cartService.addCartItem(anyLong(), any(AddCartItemRequest.class))).thenReturn(response);

        // when // then
        mockMvc.perform(
                post("/carts/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L))
                .andExpect(jsonPath("$.cartBadgeCount").value(1));
    }

    @Test
    @DisplayName("장바구니에 상품 추가 시 상품 ID가 존재해야한다.")
    void addCartItemWithEmptyProductId() throws Exception {
        // given
        AddCartItemRequest request = new AddCartItemRequest(null, 1);

        // when // then
        mockMvc.perform(
                        post("/carts/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("상품 ID가 존재하지 않습니다."));

    }

    @Test
    @DisplayName("장바구니에 상품 수정(수량)한다.")
    void updateCartItemQuantity() throws Exception {
        // given
        UpdateCartItemRequest request = new UpdateCartItemRequest(1);

        CartItemResponse response = CartItemResponse.builder()
                .cartItemId(1L)
                .productId(1L)
                .productName("상품1")
                .quantity(1)
                .unitPrice(1000L)
                .subTotalPrice(1000L)
                .isSoldOut(false)
                .quantityOver(false)
                .build();

        when(cartService.updateCartItem(1L, 1L, 1L, request)).thenReturn(response);

        // when // then
        mockMvc.perform(
                patch("/carts/{cartId}/items/{cartItemId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.cartItemId").value(1L))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.productName").value("상품1"))
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.unitPrice").value(1000L))
                .andExpect(jsonPath("$.subTotalPrice").value(1000L))
                .andExpect(jsonPath("$.isSoldOut").value(false))
                .andExpect(jsonPath("$.quantityOver").value(false));
    }

    @Test
    @DisplayName("장바구니에 상품 수정(수량) 시 수량은 1개 이상이어야 한다.")
    void addCartItemWithPositiveQuantity() throws Exception {
        // given
        UpdateCartItemRequest request = new UpdateCartItemRequest(0);

        // when // then
        mockMvc.perform(
                        patch("/carts/{cartId}/items/{cartItemId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("수량은 1개 이상이어야 합니다."));

    }

    @Test
    @DisplayName("장바구니에 상품을 삭제한다.")
    void deleteCartItem() throws Exception {
        // given
        DeleteCartItemRequest request = new DeleteCartItemRequest(List.of(1L));

        // when // then
        mockMvc.perform(
                delete("/carts/{cartId}/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니에 상품을 삭제한다.")
    void deleteCartItemWithEmptyCartItemIds() throws Exception {
        // given
        DeleteCartItemRequest request = new DeleteCartItemRequest(List.of());

        // when // then
        mockMvc.perform(
                        delete("/carts/{cartId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("삭제할 상품을 선택해주세요."));
    }
}