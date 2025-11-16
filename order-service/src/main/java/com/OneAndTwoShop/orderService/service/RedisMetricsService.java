package com.OneAndTwoShop.orderService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisMetricsService {

    private final StringRedisTemplate stringRedisTemplate;

    // 指標 key 命名規則
    private static final String METRIC_CACHE_USER_HIT = "metrics:cache:user:hit";
    private static final String METRIC_CACHE_USER_MISS = "metrics:cache:user:miss";
    private static final String METRIC_CACHE_PRODUCT_HIT = "metrics:cache:product:hit";
    private static final String METRIC_CACHE_PRODUCT_MISS = "metrics:cache:product:miss";

    private static final String METRIC_LOCK_ORDER_ACQUIRED = "metrics:lock:order:acquired";
    private static final String METRIC_LOCK_ORDER_FAILED = "metrics:lock:order:failed";

    private static final String METRIC_MQ_RETRY = "metrics:mq:order:retry";

    public void incUserCacheHit() { increment(METRIC_CACHE_USER_HIT); }
    public void incUserCacheMiss() { increment(METRIC_CACHE_USER_MISS); }

    public void incProductCacheHit() { increment(METRIC_CACHE_PRODUCT_HIT); }
    public void incProductCacheMiss() { increment(METRIC_CACHE_PRODUCT_MISS); }

    public void incOrderLockAcquired() { increment(METRIC_LOCK_ORDER_ACQUIRED); }
    public void incOrderLockFailed() { increment(METRIC_LOCK_ORDER_FAILED); }

    public void incMqRetry() { increment(METRIC_MQ_RETRY); }

    private void increment(String key) {
        try {
            stringRedisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.warn("⚠️ 更新 Redis 指標失敗：key={}, error={}", key, e.getMessage());
        }
    }

    // 提供查詢統計數值的方法
    public long getLong(String key) {
        String v = stringRedisTemplate.opsForValue().get(key);
        if (v == null) return 0L;
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public long getUserCacheHit() { return getLong(METRIC_CACHE_USER_HIT); }
    public long getUserCacheMiss() { return getLong(METRIC_CACHE_USER_MISS); }
    public long getProductCacheHit() { return getLong(METRIC_CACHE_PRODUCT_HIT); }
    public long getProductCacheMiss() { return getLong(METRIC_CACHE_PRODUCT_MISS); }
    public long getOrderLockAcquired() { return getLong(METRIC_LOCK_ORDER_ACQUIRED); }
    public long getOrderLockFailed() { return getLong(METRIC_LOCK_ORDER_FAILED); }
    public long getMqRetryCount() { return getLong(METRIC_MQ_RETRY); }
}
