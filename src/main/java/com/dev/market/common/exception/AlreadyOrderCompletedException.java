package com.dev.market.common.exception;

import com.dev.market.common.exception.enums.ErrorCode;

public class AlreadyOrderCompletedException extends CustomException {

    public AlreadyOrderCompletedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
