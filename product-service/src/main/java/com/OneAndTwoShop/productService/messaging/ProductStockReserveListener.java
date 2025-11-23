package com.OneAndTwoShop.productService.messaging;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.messaging.event.StockReserveRequestEvent;
import com.OneAndTwoShop.commonLib.messaging.event.StockReserveResultEvent;
import com.OneAndTwoShop.productService.config.RabbitMqConfig;
import com.OneAndTwoShop.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductStockReserveListener {

    private final ProductService productService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_STOCK_RESERVE_REQUEST)
    public void onStockReserveRequest(StockReserveRequestEvent event) {

        log.info("[Product] Received stock reserve request orderNo={}", event.getOrderNo());

        StockReserveResultEvent result = new StockReserveResultEvent();
        result.setOrderNo(event.getOrderNo());

        try {
            productService.reserveStockForOrder(event);
            result.setSuccess(true);
            result.setReasonKey(null);

        } catch (BusinessException be) {
            log.error("[Product] BusinessException key={}", be.getKey());
            result.setSuccess(false);
            result.setReasonKey(be.getKey());

        } catch (Exception ex) {
            log.error("[Product] Unknown error when reserve stock", ex);
            result.setSuccess(false);
            result.setReasonKey("system.error");
        }

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.RK_STOCK_RESERVE_RESULT,
                result
        );
    }
}