package com.OneAndTwoShop.orderService.message.publisher;

import com.OneAndTwoShop.commonLib.messaging.event.StockReserveRequestEvent;
import com.OneAndTwoShop.orderService.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStockReservePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(StockReserveRequestEvent event) {
        log.info("[OrderStockReservePublisher] Send reserve event orderNo={}", event.getOrderNo());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.RK_STOCK_RESERVE_REQUEST,
                event
        );
    }
}