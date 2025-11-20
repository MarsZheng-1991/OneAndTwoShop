package com.OneAndTwoShop.commonLib.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiData<T> {
    private String key;  // 對應 DB 的 key，如 system.success / user.created
    private T data;      // 真正的資料，可為 null

    public static <T> ApiData<T> success(T data) {
        return new ApiData<>("system.success", data);
    }

    public static ApiData<String> successMessage(String key) {
        return new ApiData<>(key, null);
    }
}