package com.allra.market.common.exception;

import com.allra.market.common.exception.enums.ErrorCode;

public class NotFoundException extends CustomException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
