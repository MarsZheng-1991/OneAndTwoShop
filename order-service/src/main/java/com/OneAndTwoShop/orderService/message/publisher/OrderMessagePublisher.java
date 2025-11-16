package com.OneAndTwoShop.orderService.message.publisher;

import com.OneAndTwoShop.orderService.config.RabbitMQConfig;
import com.OneAndTwoShop.orderService.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publishOrderCreated(Order order) {
        try {
            String json = objectMapper.writeValueAsString(order);
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, json);
            log.info("📤 Order Created Message Sent: {}", json);
        } catch (JsonProcessingException e) {
            log.error("❌ Failed to serialize order: {}", e.getMessage());
        }
    }
}
