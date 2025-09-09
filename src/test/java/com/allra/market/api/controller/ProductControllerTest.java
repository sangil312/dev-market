package com.allra.market.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.allra.market.api.ControllerTestSupport;
import com.allra.market.domain.product.request.ProductSearchCondition;
import com.allra.market.domain.product.response.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;

class ProductControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("상품 목록을 조회한다.")
    void searchProducts() throws Exception {
        // given
        ProductSearchCondition condition = ProductSearchCondition.builder()
                .categoryId(2L)
                .productName("아이폰")
                .minPrice(10_000L)
                .maxPrice(2_000_000L)
                .build();

        ProductResponse content = ProductResponse.builder()
                .productId(3L)
                .categoryId(2L)
                .productName("아이폰")
                .price(1_900_000L)
                .productQuantity(0)
                .isSoldOut(true)
                .build();

        PageImpl<ProductResponse> response = new PageImpl<>(
                List.of(content),
                PageRequest.of(0, 10),
                10
        );

        when(productService.searchProducts(any(ProductSearchCondition.class), any(Pageable.class)))
                .thenReturn(response);

        // when // then
        mockMvc.perform(
                get("/products")
                        .content(objectMapper.writeValueAsString(condition))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(jsonPath("$.content[0].productId").value(3L))
                .andExpect(jsonPath("$.content[0].categoryId").value(2L))
                .andExpect(jsonPath("$.content[0].productName").value("아이폰"))
                .andExpect(jsonPath("$.content[0].price").value(1_900_000L))
                .andExpect(jsonPath("$.content[0].productQuantity").value(0))
                .andExpect(jsonPath("$.content[0].isSoldOut").value(true));
    }
}