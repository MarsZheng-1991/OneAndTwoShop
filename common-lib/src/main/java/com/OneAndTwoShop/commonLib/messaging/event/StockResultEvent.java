package com.OneAndTwoShop.commonLib.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResultEvent {
    private Long orderId;
    private boolean success;
    private String reasonKey;
}