package com.OneAndTwoShop.productService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String productCode;
    private String name;
    private BigDecimal price;

    public static ProductResponse fromCacheString(String cacheValue) {
        String[] parts = cacheValue.split("\\|");
        return new ProductResponse("unknown", parts[0], new BigDecimal(parts[1]));
    }
}