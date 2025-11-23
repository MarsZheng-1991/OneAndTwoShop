package com.OneAndTwoShop.commonLib.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDecreaseEvent {

    private Long orderId;
    private List<StockItemPayload> items;
}