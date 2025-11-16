package com.OneAndTwoShop.orderService.service;

import com.OneAndTwoShop.orderService.dto.OrderDto;
import com.OneAndTwoShop.orderService.message.publisher.OrderMessagePublisher;
import com.OneAndTwoShop.orderService.model.Order;
import com.OneAndTwoShop.orderService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderMessagePublisher orderMessagePublisher;
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductServiceClient productServiceClient;
    @Autowired
    private RedisMetricsService redisMetricsService;

    private static final String ORDER_LOCK_KEY_PREFIX = "lock:order:";

    public Mono<Order> createOrder(OrderDto request) {
        String userId = request.getUserId();
        String lockKey = ORDER_LOCK_KEY_PREFIX + userId;

        return acquireLock(lockKey, 10)
                .flatMap(locked -> {
                    if (!locked) {
                        log.warn("⚠️ 使用者正在建立訂單中，拒絕重複建立: userId={}", userId);
                        return Mono.error(new RuntimeException("order.duplicate"));
                    }

                    return userVerificationService.verifyUserExists(userId)
                            .flatMap(valid -> {
                                if (!valid) return Mono.error(new RuntimeException("user.notFound"));

                                return productServiceClient.getProductPrice(request.getProductCode())
                                        .flatMap(price -> {
                                            Order order = Order.builder()
                                                    .userId(userId)
                                                    .productId(request.getProductCode())
                                                    .quantity(request.getQuantity())
                                                    .totalAmount(price.multiply(BigDecimal.valueOf(request.getQuantity())))
                                                    .createdAt(LocalDateTime.now())
                                                    .build();

                                            return Mono.fromCallable(() -> orderRepository.save(order))
                                                    .doOnSuccess(saved -> {
                                                        orderMessagePublisher.publishOrderCreated(saved);
                                                        log.info("✅ 訂單已儲存並發送 MQ：{}", saved);
                                                    });
                                        });
                            });
                })
                .doFinally(signal -> {
                    stringRedisTemplate.delete(lockKey);
                    log.info("🔓 釋放訂單鎖：{}", lockKey);
                });
    }

    private Mono<Boolean> acquireLock(String lockKey, long ttlSeconds) {
        return Mono.fromCallable(() -> {
            Boolean success = stringRedisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", ttlSeconds, TimeUnit.SECONDS);
            boolean locked = (success != null && success);
            if (locked) redisMetricsService.incOrderLockAcquired();
            else redisMetricsService.incOrderLockFailed();
            return locked;
        }).onErrorResume(e -> {
            log.error("❌ 嘗試取得訂單鎖時發生 Redis 錯誤: {}", e.getMessage());
            redisMetricsService.incOrderLockFailed();
            return Mono.just(true); // 避免阻塞流程，可改策略
        });
    }
}