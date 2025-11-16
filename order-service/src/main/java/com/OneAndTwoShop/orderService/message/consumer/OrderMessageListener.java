package com.OneAndTwoShop.orderService.message.consumer;

import com.OneAndTwoShop.orderService.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMessageListener {

    private final StringRedisTemplate redis;
    private static final String RETRY_KEY_PREFIX = "retry:order:";
    private static final int MAX_RETRY = 3;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE,
            containerFactory = "manualAckListenerContainerFactory")
    public void onMessage(Message msg, com.rabbitmq.client.Channel ch) throws IOException {
        long tag = msg.getMessageProperties().getDeliveryTag();
        String body = new String(msg.getBody());
        String orderId = JsonPath.read(body, "$.id").toString(); // 可改你自己的解析
        String retryKey = RETRY_KEY_PREFIX + orderId;

        try {
            // TODO：你的商業邏輯（可能會丟例外）
            log.info("📥 consume order: {}", body);
            ch.basicAck(tag, false);
            redis.delete(retryKey);
        } catch (Exception ex) {
            Long result = redis.opsForValue().increment(retryKey);
            long count = (result != null ? result : 0L);
            if (count <= MAX_RETRY) {
                log.warn("🔁 consume failed, retry {}/{}: {}", count, MAX_RETRY, ex.getMessage());
                ch.basicNack(tag, false, false); // 不重回主佇列，交給 x-dead-letter 轉入 retry queue
            } else {
                log.error("☠️ retry exceeded, send to DLQ: {}", orderId);
                // 直接送 DLX
                ch.basicPublish(RabbitMQConfig.ORDER_DEAD_EXCHANGE,
                        RabbitMQConfig.ORDER_DEAD_ROUTING_KEY,
                        null, msg.getBody());
                ch.basicAck(tag, false); // Ack 掉這筆，避免重複
                redis.delete(retryKey);
            }
        }
    }
}