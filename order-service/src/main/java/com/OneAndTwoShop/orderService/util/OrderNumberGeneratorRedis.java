package com.OneAndTwoShop.orderService.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OrderNumberGeneratorRedis {

    private final StringRedisTemplate redis;

    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 使用 Redis INCR 生成訂單編號：
     *
     * 1. 每天都使用新的 Redis key，例如：
     *      order:no:20251123
     *
     * 2. Redis INCR 天然具有：
     *      - 原子性（不會產生衝突）
     *      - 分散式（多台 service 同時安全遞增）
     *      - 重啟不會重置（counter 在 Redis，不在 JVM）
     *
     * 3. EXPIRE 24 小時確保 key 自動清掉（隔天自然重新從 1 開始）
     *
     * 4. 最終組成訂單編號：
     *      ORD + yyyyMMdd + 3 位數流水號
     *      例：ORD20251123001
     */
    public String generateOrderNo() {

        // 1. 今日日期字串（20251123）
        String today = LocalDate.now().format(DATE_FMT);

        // 2. Redis key（每天不同）
        String key = "order:no:" + today;

        // 3. Redis 原子遞增（分散式系統保證不重複）
        Long seq = redis.opsForValue().increment(key);

        // 第一次產生時，設定 24 小時自動過期
        if (seq != null && seq == 1L) {
            redis.expire(key, java.time.Duration.ofDays(1));
        }

        // 4. 組成完整訂單編號
        return PREFIX + today + String.format("%03d", seq);
    }
}