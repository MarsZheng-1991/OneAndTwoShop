package com.OneAndTwoShop.orderService.message.consumer;

import com.OneAndTwoShop.commonLib.messaging.event.StockResultEvent;
import com.OneAndTwoShop.orderService.model.Order;
import com.OneAndTwoShop.orderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStockResultListener {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = "stock.decrease.success")
    public void handleSuccess(StockResultEvent event) {
        log.info("[SAGA] Stock decreased OK for order {}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow();
        order.setStatus("CREATED");
        orderRepository.save(order);
    }

    @RabbitListener(queues = "stock.decrease.fail")
    public void handleFail(StockResultEvent event) {
        log.warn("[SAGA] Stock decrease FAIL for order {}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId()).orElseThrow();
        order.setStatus("FAILED");
        orderRepository.save(order);
    }
}