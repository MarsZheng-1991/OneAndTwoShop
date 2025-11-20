package com.OneAndTwoShop.commonLib.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiData<T> {

    private String message; // 已翻譯好的訊息
    private T data;
}