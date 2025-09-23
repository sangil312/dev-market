package com.dev.market.common.exception.dto;

import com.dev.market.common.exception.CustomException;
import com.dev.market.common.exception.enums.ErrorCode;

public class ExternalApiException extends CustomException {

    public ExternalApiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
