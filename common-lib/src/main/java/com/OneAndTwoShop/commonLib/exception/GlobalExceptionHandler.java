package com.OneAndTwoShop.commonLib.exception;

import com.OneAndTwoShop.commonLib.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorMessageResolver messageResolver;

    public GlobalExceptionHandler(ErrorMessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        String message = messageResolver.resolve(ex.getErrorCode());
        return ResponseEntity.ok(
                ApiResponse.fail(
                        HttpStatus.BAD_REQUEST.value(),
                        message,
                        null
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        return ResponseEntity.ok(
                ApiResponse.fail(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        messageResolver.resolve("INTERNAL_ERROR"),
                        null
                )
        );
    }
}
