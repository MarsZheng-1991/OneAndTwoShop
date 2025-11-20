package com.OneAndTwoShop.commonLib.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private int code;  // 一般放 HTTP status code
    private T data;
}