package com.OneAndTwoShop.commonLib.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemPayload {
    private Long productId;
    private Integer quantity;
}