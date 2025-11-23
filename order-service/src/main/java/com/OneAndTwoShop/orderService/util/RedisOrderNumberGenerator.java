package com.OneAndTwoShop.orderService.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 使用 Redis 產生訂單編號（最推薦）
 *
 * 為什麼這樣做？
 * --------------------------------------
 * 1. Redis INCR 是原子操作 → 多台服務也不會產生相同序號
 * 2. 訂單編號格式：ORD + yyyyMMdd + 流水號
 * 3. 每天使用不同的 key，例如：
 *      order:seq:20251123 -> 1
 *      order:seq:20251123 -> 2
 *      ...
 *      order:seq:20251124 -> 1 (隔天重新開始)
 * 4. Redis 重啟後仍會保留序號，不會衝突
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisOrderNumberGenerator {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 使用 Redis INCR 產生訂單編號
     */
    public String generateOrderNo() {

        // 產生今日日期字串，例如：20251123
        String today = LocalDate.now().format(DATE_FMT);

        // Redis Key → 每天一支 key
        String redisKey = "order:seq:" + today;

        // Redis INCR → 原子性遞增，回傳 long
        Long seq = redisTemplate.opsForValue().increment(redisKey);

        // 序號補滿三位數：001, 002, 003...
        String number = String.format("%04d", seq);

        String orderNo = PREFIX + today + number;

        log.info("Generated OrderNo => {}", orderNo);

        return orderNo;
    }
}