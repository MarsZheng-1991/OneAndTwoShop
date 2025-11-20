package com.OneAndTwoShop.commonLib.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int code; // HTTP 狀態碼
    private T data;   // 包含成功資料 或 message（已翻譯）
}