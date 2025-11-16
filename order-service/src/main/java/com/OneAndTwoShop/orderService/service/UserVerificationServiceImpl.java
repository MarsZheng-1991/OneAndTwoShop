package com.OneAndTwoShop.orderService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserVerificationServiceImpl implements UserVerificationService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMetricsService redisMetricsService;

    private static final String USER_CACHE_KEY_PREFIX = "user:";

    @Override
    public Mono<Boolean> verifyUserExists(String userId) {
        log.info("🔍 驗證使用者是否存在: {}", userId);
        String cacheKey = USER_CACHE_KEY_PREFIX + userId;

        return Mono.fromCallable(() -> {
                    String cached = stringRedisTemplate.opsForValue().get(cacheKey);
                    if (cached != null) {
                        redisMetricsService.incUserCacheHit();
                        return Boolean.valueOf(cached);
                    }

                    redisMetricsService.incUserCacheMiss();

                    boolean exists = true; // TODO: 改成呼叫 user-service

                    stringRedisTemplate.opsForValue()
                            .set(cacheKey, String.valueOf(exists), Duration.ofMinutes(5));

                    return exists;
                })
                .onErrorResume(e -> {
                    log.error("❌ 驗證使用者時 Redis 錯誤: {}", e.getMessage());
                    // 不快取 fallback
                    return Mono.just(Boolean.TRUE);
                });
    }
}
