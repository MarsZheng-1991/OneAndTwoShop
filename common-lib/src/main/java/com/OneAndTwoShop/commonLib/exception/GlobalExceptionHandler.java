package com.OneAndTwoShop.commonLib.exception;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.OneAndTwoShop.commonLib.common.i18n.TranslateService;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final TranslateService translateService;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        Locale locale = getLocale(request);
        String message = translateService.translate(ex.getKey(), locale);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleOtherException(
            Exception ex,
            HttpServletRequest request) {

        log.error("[Unhandled Exception]", ex);

        Locale locale = getLocale(request);
        String message = translateService.translate("system.error", locale);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(500, message));
    }

    private Locale getLocale(HttpServletRequest request) {
        String lang = request.getHeader("Accept-Language");
        return (lang != null) ? Locale.forLanguageTag(lang) : Locale.TAIWAN;
    }
}