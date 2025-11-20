package com.OneAndTwoShop.productService.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductQueryRequest {

    private String name;          // 商品名稱（模糊搜尋）
    private String productCode;   // 商品代碼
    private String category;      // 類別
    private String status;        // active / inactive

    private BigDecimal minPrice;  // 最低價格
    private BigDecimal maxPrice;  // 最高價格

    private LocalDateTime startDate; // 建立時間起
    private LocalDateTime endDate;   // 建立時間迄
}
