package com.allra.market.common.exception;

import com.allra.market.common.exception.enums.ErrorCode;

public class QuantityOverException extends CustomException {

    public QuantityOverException(ErrorCode errorCode) {
        super(errorCode);
    }
}
