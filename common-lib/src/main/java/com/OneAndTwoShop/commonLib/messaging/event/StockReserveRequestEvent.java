package com.OneAndTwoShop.commonLib.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockReserveRequestEvent {

    private String orderNo;                 // 訂單編號
    private Long userId;                    // 下單會員
    private List<StockItemPayload> items;   // 要預扣的商品清單
}