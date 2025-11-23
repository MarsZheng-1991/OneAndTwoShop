package com.OneAndTwoShop.orderService.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductInfo {
    private Long id;
    private String productCode;
    private String name;
    private BigDecimal price;
    private Integer stock;
}