package com.OneAndTwoShop.commonLib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class StockDecreaseMessage {

    private Long orderId;
    private String orderNo;
    private List<Item> items;

    @Data
    @AllArgsConstructor
    public static class Item {
        private Long productId;
        private Integer quantity;
    }
}