package com.OneAndTwoShop.orderService.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 設定：
 * - 使用 Topic Exchange（主題交換機）
 * - 用於訂單相關事件通知，例如「訂單建立」
 */
@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    // 延遲重試（10s）
    public static final String ORDER_RETRY_EXCHANGE = "order.retry.exchange";
    public static final String ORDER_RETRY_QUEUE_10S = "order.retry.10s.queue";
    public static final String ORDER_RETRY_ROUTING_KEY_10S = "order.retry.10s";

    // DLQ
    public static final String ORDER_DEAD_EXCHANGE = "order.dead.exchange";
    public static final String ORDER_DEAD_QUEUE = "order.dead.queue";
    public static final String ORDER_DEAD_ROUTING_KEY = "order.dead";

    public static final String ORDER_ROUTING_KEY = "order.created";

    public static final String EXCHANGE = "oneshop.order.exchange";

    public static final String QUEUE_STOCK_RESERVE_REQUEST = "order.stock.reserve.request.queue";
    public static final String QUEUE_STOCK_RESERVE_RESULT = "order.stock.reserve.result.queue";

    public static final String RK_STOCK_RESERVE_REQUEST = "order.stock.reserve.request";
    public static final String RK_STOCK_RESERVE_RESULT = "order.stock.reserve.result";

    // ============================
    // 1. JSON Converter
    // ============================
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ============================
    // 2. Publisher (RabbitTemplate)
    // ============================
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    // ============================
    // 3. Consumer Listener Factory
    // ============================
    @Bean(name = "manualAckListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory manualAckListenerContainerFactory(
            ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(1);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }


    // ============================
    // Exchange
    // ============================
    @Bean
    public TopicExchange orderExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    DirectExchange retryExchange() {
        return new DirectExchange(ORDER_RETRY_EXCHANGE);
    }

    @Bean
    DirectExchange deadExchange() {
        return new DirectExchange(ORDER_DEAD_EXCHANGE);
    }

    // ============================
    // Queue
    // ============================
    @Bean
    Queue orderQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_RETRY_ROUTING_KEY_10S)
                .build();
    }

    @Bean
    Queue retry10sQueue() {
        return QueueBuilder.durable(ORDER_RETRY_QUEUE_10S)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", ORDER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_CREATED_ROUTING_KEY)
                .build();
    }

    @Bean
    Queue deadQueue() {
        return QueueBuilder.durable(ORDER_DEAD_QUEUE).build();
    }

    // ============================
    // Binding
    // ============================
    @Bean
    Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    Binding retry10sBinding() {
        return BindingBuilder.bind(retry10sQueue())
                .to(retryExchange())
                .with(ORDER_RETRY_ROUTING_KEY_10S);
    }

    @Bean
    Binding deadBinding() {
        return BindingBuilder.bind(deadQueue())
                .to(deadExchange())
                .with(ORDER_DEAD_ROUTING_KEY);
    }

    @Bean
    public Queue orderStockDecreaseQueue() {
        return new Queue("order.stock.decrease", true);
    }

    @Bean
    public Queue stockDecreaseSuccessQueue() {
        return new Queue("stock.decrease.success", true);
    }

    @Bean
    public Queue stockDecreaseFailQueue() {
        return new Queue("stock.decrease.fail", true);
    }

    @Bean
    public Queue stockReserveRequestQueue() {
        return QueueBuilder.durable(QUEUE_STOCK_RESERVE_REQUEST).build();
    }

    @Bean
    public Queue stockReserveResultQueue() {
        return QueueBuilder.durable(QUEUE_STOCK_RESERVE_RESULT).build();
    }

    @Bean
    public Binding stockReserveRequestBinding() {
        return BindingBuilder.bind(stockReserveRequestQueue())
                .to(orderExchange())
                .with(RK_STOCK_RESERVE_REQUEST);
    }

    @Bean
    public Binding stockReserveResultBinding() {
        return BindingBuilder.bind(stockReserveResultQueue())
                .to(orderExchange())
                .with(RK_STOCK_RESERVE_RESULT);
    }
}