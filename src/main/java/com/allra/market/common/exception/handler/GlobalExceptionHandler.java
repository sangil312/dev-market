package com.allra.market.common.exception.handler;

import com.allra.market.common.exception.CustomException;
import com.allra.market.common.exception.dto.ErrorResponse;
import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        BindingResult bindingResult = ex.getBindingResult();
        String message = Objects.requireNonNull(bindingResult.getFieldError())
                .getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustomException(CustomException ex) {
        return ErrorResponse.of(
                ex.getErrorCode().getHttpStatus(),
                ex.getErrorCode().getMessage()
        );
    }
}
