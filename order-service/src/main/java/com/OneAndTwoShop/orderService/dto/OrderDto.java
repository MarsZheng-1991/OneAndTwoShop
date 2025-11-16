package com.OneAndTwoShop.orderService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderDto {

    @NotBlank(message = "{order.userId.notBlank}")
    private String userId;

    @NotBlank(message = "{order.productCode.notBlank}")
    private String productCode;

    @Min(value = 1, message = "{order.quantity.min}")
    private Integer quantity;
}
