package com.OneAndTwoShop.commonLib.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;             // HTTP 狀態碼
    private boolean success;        // 是否成功
    private String message;         // 顯示訊息
    private T data;                 // 真正的資料
    private LocalDateTime timestamp; // 回傳時間

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> fail(int status, String message, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .success(false)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
