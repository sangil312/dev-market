package com.allra.market.common.exception.dto;

import com.allra.market.common.exception.CustomException;
import com.allra.market.common.exception.enums.ErrorCode;

public class ExternalApiException extends CustomException {

    public ExternalApiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
