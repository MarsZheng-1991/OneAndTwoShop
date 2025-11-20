package com.OneAndTwoShop.commonLib.common.error;

/**
 * 很簡單的業務例外類別。
 * 只保存一個 errorCode，之後用來查 i18n 訊息。
 */
public class BusinessException extends RuntimeException {

    private final String key;

    public BusinessException(String key) {
        super(key);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}