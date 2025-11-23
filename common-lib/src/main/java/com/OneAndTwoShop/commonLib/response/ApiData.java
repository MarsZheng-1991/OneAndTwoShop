package com.OneAndTwoShop.commonLib.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiData<T> {

    private String message;  // 顯示給前端的訊息（多語系後的結果）
    private T data;          // 實際資料（例如 user、product、page…）

    // -------------------------------------------------------
    // 通用成功（含資料）
    // -------------------------------------------------------
    public static <T> ApiData<T> success(String message, T data) {
        return new ApiData<>(message, data);
    }

    // -------------------------------------------------------
    // 成功但沒有資料（例如刪除成功）
    // -------------------------------------------------------
    public static <T> ApiData<T> successMessage(String message) {
        return new ApiData<>(message, null);
    }

    // -------------------------------------------------------
    // 成功（多語系版本）
    // message: 已翻譯完成的字串
    // -------------------------------------------------------
    public static <T> ApiData<T> successMessage(String message, String locale) {
        // locale 在 Service 已經處理翻譯，這裡不需再用 locale
        return new ApiData<>(message, null);
    }

    // -------------------------------------------------------
    // 成功回傳資料，但 message 已經是翻譯後的字串
    // -------------------------------------------------------
    public static <T> ApiData<T> successData(String message, T data) {
        return new ApiData<>(message, data);
    }
}