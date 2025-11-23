package com.OneAndTwoShop.commonLib.messaging.event;

import lombok.Data;

@Data
public class StockReserveItem {
    private Long productId;
    private Integer quantity;
}