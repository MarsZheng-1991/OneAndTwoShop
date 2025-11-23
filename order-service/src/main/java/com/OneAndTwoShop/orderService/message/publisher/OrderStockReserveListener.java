package com.OneAndTwoShop.orderService.message.publisher;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.messaging.event.StockReserveResultEvent;
import com.OneAndTwoShop.orderService.config.RabbitMQConfig;
import com.OneAndTwoShop.orderService.model.Order;
import com.OneAndTwoShop.orderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStockReserveListener {

    private final OrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_STOCK_RESERVE_RESULT)
    @Transactional
    public void onStockReserveResult(StockReserveResultEvent event) {

        log.info("[OrderStockReserveListener] Receive result orderNo={}, success={}",
                event.getOrderNo(), event.isSuccess());

        Order order = orderRepository.findByOrderNo(event.getOrderNo())
                .orElseThrow(() -> new BusinessException("order.notfound"));

        if (event.isSuccess()) {
            order.setStatus("CREATED");
        } else {
            order.setStatus("FAILED");
        }

        orderRepository.save(order);
    }
}