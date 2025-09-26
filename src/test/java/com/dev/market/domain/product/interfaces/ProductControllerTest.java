package com.dev.market.domain.product.interfaces;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

import com.dev.market.ControllerTestSupport;
import com.dev.market.domain.product.application.request.ProductSearchServiceRequest;
import com.dev.market.domain.product.application.response.ProductResponse;
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

        when(productService.searchProducts(any(ProductSearchServiceRequest.class), any(Pageable.class)))
                .thenReturn(response);

        // when // then
        mockMvc.perform(
                        get("/api/products")
                                .queryParam("categoryId", "1")
                                .queryParam("productName", "상품1")
                                .queryParam("minPrice", "1000")
                                .queryParam("maxPrice", "2000")
                                .queryParam("size", "20")
                                .queryParam("page", "0")
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.content[0].productId").value(1L))
                .andExpect(jsonPath("$.content[0].categoryId").value(1L))
                .andExpect(jsonPath("$.content[0].productName").value("상품1"))
                .andExpect(jsonPath("$.content[0].price").value(1000L))
                .andExpect(jsonPath("$.content[0].productQuantity").value(10))
                .andExpect(jsonPath("$.content[0].isSoldOut").value(false))
                // REST Docs
                .andDo(document("product-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("categoryId").description("카테고리 ID").optional(),
                                parameterWithName("productName").description("상품 이름").optional(),
                                parameterWithName("minPrice").description("상품 최소 가격").optional(),
                                parameterWithName("maxPrice").description("상품 최대 가격").optional(),
                                parameterWithName("page").description("페이지").optional(),
                                parameterWithName("size").description("페이지 당 목록 갯수").optional()
                        ),
                        responseFields(
                                fieldWithPath("content[].productId").description("상품 ID"),
                                fieldWithPath("content[].categoryId").description("카테고리 ID"),
                                fieldWithPath("content[].productName").description("상품명"),
                                fieldWithPath("content[].price").description("가격"),
                                fieldWithPath("content[].productQuantity").description("재고 수량"),
                                fieldWithPath("content[].isSoldOut").description("품절 여부"),

                                fieldWithPath("pageable.pageNumber").description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("pageable.sort.empty").description("정렬 없음 여부"),
                                fieldWithPath("pageable.sort.sorted").description("정렬 적용 여부"),
                                fieldWithPath("pageable.sort.unsorted").description("정렬 미적용 여부"),
                                fieldWithPath("pageable.offset").description("오프셋"),
                                fieldWithPath("pageable.paged").description("페이지네이션 사용 여부"),
                                fieldWithPath("pageable.unpaged").description("페이지네이션 미사용 여부"),

                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 데이터 개수"),
                                fieldWithPath("size").description("현재 페이지 크기"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("sort.empty").description("정렬 없음 여부"),
                                fieldWithPath("sort.sorted").description("정렬 적용 여부"),
                                fieldWithPath("sort.unsorted").description("정렬 미적용 여부"),
                                fieldWithPath("first").description("첫 페이지 여부"),
                                fieldWithPath("numberOfElements").description("현재 페이지의 요소 개수"),
                                fieldWithPath("empty").description("비어있는 페이지 여부")
                        )
                )
        );
    }
}