package com.OneAndTwoShop.orderService.controller;

import com.OneAndTwoShop.orderService.dto.OrderDto;
import com.OneAndTwoShop.orderService.message.publisher.OrderMessagePublisher;
import com.OneAndTwoShop.orderService.model.Order;
import com.OneAndTwoShop.orderService.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMessagePublisher orderMessagePublisher;

    @PostMapping
    public Mono<ResponseEntity<String>> createOrder(@Valid @RequestBody OrderDto dto) {
        return orderService.createOrder(dto)
                .map(order -> ResponseEntity.ok("Order created"))
                .onErrorResume(e -> {
                    log.error("Order creation failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("訂單建立失敗：" + e.getMessage()));
                });
    }

    @PostMapping("/test/mq")
    public ResponseEntity<String> testMq(@RequestBody Order order) {
        orderMessagePublisher.publishOrderCreated(order);
        return ResponseEntity.ok("📤 訂單訊息已送出到 MQ！");
    }
}
