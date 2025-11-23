package com.OneAndTwoShop.productService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "oneshop.order.exchange";

    public static final String QUEUE_STOCK_RESERVE_REQUEST = "order.stock.reserve.request.queue";
    public static final String QUEUE_STOCK_RESERVE_RESULT  = "order.stock.reserve.result.queue";

    public static final String RK_STOCK_RESERVE_REQUEST = "order.stock.reserve.request";
    public static final String RK_STOCK_RESERVE_RESULT  = "order.stock.reserve.result";

    @Bean
    public TopicExchange orderExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
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
    public Binding stockReserveRequestBinding(Queue stockReserveRequestQueue,
                                              TopicExchange orderExchange) {
        return BindingBuilder.bind(stockReserveRequestQueue)
                .to(orderExchange)
                .with(RK_STOCK_RESERVE_REQUEST);
    }

    @Bean
    public Binding stockReserveResultBinding(Queue stockReserveResultQueue,
                                             TopicExchange orderExchange) {
        return BindingBuilder.bind(stockReserveResultQueue)
                .to(orderExchange)
                .with(RK_STOCK_RESERVE_RESULT);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory cf,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(messageConverter);
        return template;
    }
}