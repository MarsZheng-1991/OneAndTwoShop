package com.OneAndTwoShop.commonLib.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockReserveResultEvent {

    private String orderNo;     // 對應的訂單編號
    private boolean success;    // 是否預扣成功
    private String reasonKey;   // 失敗原因的 i18n key（例如 product.stock.notenough）
}