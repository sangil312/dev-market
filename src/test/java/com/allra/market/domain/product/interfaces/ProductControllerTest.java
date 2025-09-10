package com.allra.market.domain.product.interfaces;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.allra.market.ControllerTestSupport;
import com.allra.market.domain.product.application.dto.request.ProductSearchCondition;
import com.allra.market.domain.product.application.dto.response.ProductResponse;
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
                .categoryId(1L)
                .productName("상품1")
                .minPrice(1000L)
                .maxPrice(2000L)
                .build();

        ProductResponse content = ProductResponse.builder()
                .productId(1L)
                .categoryId(1L)
                .productName("상품1")
                .price(1000L)
                .productQuantity(10)
                .isSoldOut(false)
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.content[0].productId").value(1L))
                .andExpect(jsonPath("$.content[0].categoryId").value(1L))
                .andExpect(jsonPath("$.content[0].productName").value("상품1"))
                .andExpect(jsonPath("$.content[0].price").value(1000L))
                .andExpect(jsonPath("$.content[0].productQuantity").value(10))
                .andExpect(jsonPath("$.content[0].isSoldOut").value(false));
    }
}