package com.OneAndTwoShop.commonLib.common.error;

/**
 * 統一錯誤回傳格式。
 * 之後所有 Exception 都可以轉成這個物件回傳給前端。
 */
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private String message; // 不回 code，前端直接呈現文字
}