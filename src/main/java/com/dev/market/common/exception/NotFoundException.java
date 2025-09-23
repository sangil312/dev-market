package com.dev.market.common.exception;

import com.dev.market.common.exception.enums.ErrorCode;

public class NotFoundException extends CustomException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
