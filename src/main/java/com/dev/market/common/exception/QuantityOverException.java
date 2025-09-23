package com.dev.market.common.exception;

import com.dev.market.common.exception.enums.ErrorCode;

public class QuantityOverException extends CustomException {

    public QuantityOverException(ErrorCode errorCode) {
        super(errorCode);
    }
}
