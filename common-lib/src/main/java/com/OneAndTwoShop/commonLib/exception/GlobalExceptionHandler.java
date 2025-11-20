package com.OneAndTwoShop.commonLib.exception;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.common.i18n.ErrorMessageService;
import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.commonLib.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMessageService errorMessageService;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ApiData<Object>>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        String locale = getLocaleFromRequest(request);
        String message = errorMessageService.translate(ex.getKey(), locale);

        ApiData<Object> apiData = new ApiData<>(message, null);
        ApiResponse<ApiData<Object>> body =
                new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), apiData);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiData<Object>>> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        String locale = getLocaleFromRequest(request);
        String message = errorMessageService.translate("system.error", locale);

        ApiData<Object> apiData = new ApiData<>(message, null);
        ApiResponse<ApiData<Object>> body =
                new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiData);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String getLocaleFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Accept-Language");
        if (header == null || header.isBlank()) {
            return "zh"; // 預設
        }
        // 只取前面兩碼 "zh-TW" -> "zh"
        return header.split(",")[0].trim();
    }
}