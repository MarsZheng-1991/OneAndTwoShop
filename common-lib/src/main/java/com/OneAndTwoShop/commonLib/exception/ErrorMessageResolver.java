package com.OneAndTwoShop.commonLib.exception;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

public class ErrorMessageResolver {
    private static final Map<String, String> ERROR_MESSAGES = new HashMap<>();

    static {
        // User
        ERROR_MESSAGES.put("USER_NOT_FOUND", "無此使用者");
        ERROR_MESSAGES.put("USER_ALREADY_EXISTS", "使用者已存在");
        ERROR_MESSAGES.put("USER_INVALID_CREDENTIALS", "帳號或密碼錯誤");

        // Product
        ERROR_MESSAGES.put("PRODUCT_NOT_FOUND", "找不到該商品");
        ERROR_MESSAGES.put("PRODUCT_OUT_OF_STOCK", "商品庫存不足");

        // Common
        ERROR_MESSAGES.put("INTERNAL_ERROR", "系統發生未知錯誤");
    }

    public String resolve(String key) {
        return ERROR_MESSAGES.getOrDefault(key, "未知錯誤（" + key + "）");
    }
}
