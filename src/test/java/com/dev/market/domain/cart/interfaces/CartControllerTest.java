package com.dev.market.domain.cart.interfaces;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dev.market.ControllerTestSupport;
import com.dev.market.domain.cart.application.request.CartItemAddServiceRequest;
import com.dev.market.domain.cart.application.request.CartItemDeleteServiceRequest;
import com.dev.market.domain.cart.application.request.CartItemUpdateServiceRequest;
import com.dev.market.domain.cart.application.response.CartAddResponse;
import com.dev.market.domain.cart.application.response.CartItemResponse;
import com.dev.market.domain.cart.application.response.CartResponse;
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
                    get("/api/carts/me"))
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
                .andExpect(jsonPath("$.items[0].quantityOver").value(false))
                //REST Docs
                .andDo(document("my-cart-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("cartId").type(NUMBER)
                                        .description("장바구니 ID"),
                                fieldWithPath("totalItemsQuantity").type(NUMBER)
                                        .description("장바구니 상품 별 수량"),
                                fieldWithPath("totalPrice").type(NUMBER)
                                        .description("장바구니 상품 별 총 가격"),
                                fieldWithPath("items[].cartItemId").type(NUMBER)
                                        .description("장바구니 상품 ID"),
                                fieldWithPath("items[].productId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("items[].productName").type(STRING)
                                        .description("상품 이름"),
                                fieldWithPath("items[].quantity").type(NUMBER)
                                        .description("장바구니 상품 수량"),
                                fieldWithPath("items[].unitPrice").type(NUMBER)
                                        .description("장바구니 상품 개당 가격"),
                                fieldWithPath("items[].subTotalPrice").type(NUMBER)
                                        .description("장바구니 상품 가격"),
                                fieldWithPath("items[].isSoldOut").type(BOOLEAN)
                                        .description("품절 여부"),
                                fieldWithPath("items[].quantityOver").type(BOOLEAN)
                                        .description("상품 재고 부족")
                        )
                )
        );
    }

    @Test
    @DisplayName("장바구니에 상품을 추가한다.")
    void addCartItem() throws Exception {
        // given
        CartItemAddServiceRequest request = new CartItemAddServiceRequest(1L, 1);

        CartAddResponse response = new CartAddResponse(1L, 1);

        when(cartService.addCartItem(anyLong(), any(CartItemAddServiceRequest.class))).thenReturn(response);

        // when // then
        mockMvc.perform(
                post("/api/carts/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L))
                .andExpect(jsonPath("$.cartBadgeCount").value(1))
                // REST Docs
                .andDo(document("cart-add-items",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("quantity").type(NUMBER)
                                        .description("상품 수량")
                        ),
                        responseFields(
                                fieldWithPath("cartId").type(NUMBER)
                                        .description("장바구니 ID"),
                                fieldWithPath("cartBadgeCount").type(NUMBER)
                                        .description("장바구니 상품 목록 수량")
                        )
                )
        );
    }

    @Test
    @DisplayName("장바구니에 상품 추가 시 상품 ID가 존재해야한다.")
    void addCartItemWithEmptyProductId() throws Exception {
        // given
        CartItemAddServiceRequest request = new CartItemAddServiceRequest(null, 1);

        // when // then
        mockMvc.perform(
                        post("/api/carts/items")
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
        CartItemUpdateServiceRequest request = new CartItemUpdateServiceRequest(1);

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
                patch("/api/carts/{cartId}/items/{cartItemId}", 1L, 1L)
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
                .andExpect(jsonPath("$.quantityOver").value(false))
                // REST Docs
                .andDo(document("cart-update-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("cartId").description("장바구니 ID"),
                                parameterWithName("cartItemId").description("장바구니 상품 ID")
                        ),
                        requestFields(
                                fieldWithPath("quantity").type(NUMBER)
                                        .description("상품 수량")
                        ),
                        responseFields(
                                fieldWithPath("cartItemId").type(NUMBER)
                                        .description("장바구니 상품 ID"),
                                fieldWithPath("productId").type(NUMBER)
                                        .description("상품 ID"),
                                fieldWithPath("productName").type(STRING)
                                        .description("상품 이름"),
                                fieldWithPath("quantity").type(NUMBER)
                                        .description("상품 수량"),
                                fieldWithPath("unitPrice").type(NUMBER)
                                        .description("상품 가격"),
                                fieldWithPath("subTotalPrice").type(NUMBER)
                                        .description("장바구니에 담긴 상품 총 가격"),
                                fieldWithPath("isSoldOut").type(BOOLEAN)
                                        .description("상품 품절 여부"),
                                fieldWithPath("quantityOver").type(BOOLEAN)
                                        .description("상품 재고 초과")
                        )
                )
        );
    }

    @Test
    @DisplayName("장바구니에 상품 수정(수량) 시 수량은 1개 이상이어야 한다.")
    void addCartItemWithPositiveQuantity() throws Exception {
        // given
        CartItemUpdateServiceRequest request = new CartItemUpdateServiceRequest(0);

        // when // then
        mockMvc.perform(
                        patch("/api/carts/{cartId}/items/{cartItemId}", 1L, 1L)
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
        CartItemDeleteServiceRequest request = new CartItemDeleteServiceRequest(List.of(1L));

        // when // then
        mockMvc.perform(
                delete("/api/carts/{cartId}/items", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andDo(print())
                .andExpect(status().isOk())
                // REST Docs
                .andDo(document("cart-delete-items",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("cartId").description("장바구니 ID")
                        ),
                        requestFields(
                                fieldWithPath("cartItemIds[]").type(ARRAY)
                                        .description("장바구니 상품 ID")
                        )
                )
        );
    }

    @Test
    @DisplayName("장바구니에 상품 삭제 시 장바구니 아이템 ID가 존재해야한다.")
    void deleteCartItemWithEmptyCartItemIds() throws Exception {
        // given
        CartItemDeleteServiceRequest request = new CartItemDeleteServiceRequest(List.of());

        // when // then
        mockMvc.perform(
                        delete("/api/carts/{cartId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("삭제할 상품을 선택해주세요."));
    }
}