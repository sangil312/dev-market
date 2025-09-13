package com.allra.market.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 404 NOT FOUND */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에 상품이 존재하지 않습니다."),

    /* 409 CONFLICT */
    PRODUCT_QUANTITY_OVER(HttpStatus.CONFLICT, "상품 재고가 부족합니다."),

    /* 500 */
    PAYMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제에 실패했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
