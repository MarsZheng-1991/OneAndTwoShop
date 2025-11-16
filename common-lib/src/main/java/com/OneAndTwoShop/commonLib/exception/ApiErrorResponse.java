package com.OneAndTwoShop.commonLib.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;

    public ApiErrorResponse(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
