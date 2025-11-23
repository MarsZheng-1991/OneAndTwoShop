package com.OneAndTwoShop.commonLib.dto;

import com.OneAndTwoShop.commonLib.messaging.event.StockItemPayload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDecreaseEvent {
    private Long orderId;
    private List<StockItemPayload> items;
}