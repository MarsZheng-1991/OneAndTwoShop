package com.OneAndTwoShop.orderService.controller;

import com.OneAndTwoShop.orderService.service.RedisMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/internal/metrics/redis")
@RequiredArgsConstructor
public class RedisMetricsController {

    private final RedisMetricsService redisMetricsService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRedisMetrics() {
        Map<String, Object> body = new HashMap<>();

        long userHit = redisMetricsService.getUserCacheHit();
        long userMiss = redisMetricsService.getUserCacheMiss();
        long productHit = redisMetricsService.getProductCacheHit();
        long productMiss = redisMetricsService.getProductCacheMiss();
        long lockAcquired = redisMetricsService.getOrderLockAcquired();
        long lockFailed = redisMetricsService.getOrderLockFailed();
        long mqRetry = redisMetricsService.getMqRetryCount();

        body.put("cacheUserHit", userHit);
        body.put("cacheUserMiss", userMiss);
        body.put("cacheUserHitRate", calcRate(userHit, userMiss));

        body.put("cacheProductHit", productHit);
        body.put("cacheProductMiss", productMiss);
        body.put("cacheProductHitRate", calcRate(productHit, productMiss));

        body.put("lockOrderAcquired", lockAcquired);
        body.put("lockOrderFailed", lockFailed);

        body.put("mqOrderRetryCount", mqRetry);

        return ResponseEntity.ok(body);
    }

    private BigDecimal calcRate(long hit, long miss) {
        long total = hit + miss;
        if (total == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(hit * 100.0 / total).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
