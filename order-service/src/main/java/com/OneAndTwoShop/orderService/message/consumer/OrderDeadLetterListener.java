package com.OneAndTwoShop.orderService.message.consumer;

import com.OneAndTwoShop.orderService.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderDeadLetterListener {

    @RabbitListener(
            queues = RabbitMQConfig.ORDER_DEAD_QUEUE,
            containerFactory = "manualAckListenerContainerFactory"
    )
    public void receiveDeadLetter(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String body = new String(message.getBody());
        log.error("☠️ 收到死信訊息：body={}, properties={}", body, message.getMessageProperties());
        channel.basicAck(deliveryTag, false);
    }
}
