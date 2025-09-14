package com.allra.market.common.exception;

import com.allra.market.common.exception.enums.ErrorCode;

public class AlreadyOrderCompletedException extends CustomException {

    public AlreadyOrderCompletedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
