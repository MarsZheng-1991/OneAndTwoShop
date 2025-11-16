package com.OneAndTwoShop.orderService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final WebClient.Builder webClientBuilder;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMetricsService redisMetricsService;

    private static final String PRODUCT_CACHE_KEY_PREFIX = "product:";

    public Mono<BigDecimal> getProductPrice(String productCode) {
        String cacheKey = PRODUCT_CACHE_KEY_PREFIX + productCode + ":price";
        log.info("🔍 查詢商品價格開始：{}", productCode);

        return Mono.defer(() -> {
            String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey);

            if (cachedValue != null) {
                redisMetricsService.incProductCacheHit();
                log.info("💾 Cache 命中：{} -> {}", cacheKey, cachedValue);
                return Mono.just(new BigDecimal(cachedValue));
            } else {
                redisMetricsService.incProductCacheMiss();
                log.info("⚠️ Cache miss，呼叫 product-service API...");
                return callProductService(productCode, cacheKey);
            }
        });
    }

    /**
     * 這裡非常重要：
     * ❗ 不要使用 WebClient 的 .onErrorResume()
     * ❗ 不然 Resilience4j 無法偵測錯誤（Retry/CircuitBreaker 都會失效）
     */
    @Retry(name = "productApi")
    @CircuitBreaker(name = "productApi", fallbackMethod = "fallbackPrice")
    private Mono<BigDecimal> callProductService(String productCode, String cacheKey) {

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/api/products/{code}", productCode)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("❌ product-service 發生錯誤: HTTP {}, body={}",
                                        clientResponse.statusCode(), body);
                                return Mono.error(new RuntimeException("product.service.error"));
                            });
                })
                .bodyToMono(ProductResponse.class)
                .map(ProductResponse::price)
                .doOnNext(price -> {
                    stringRedisTemplate.opsForValue()
                            .set(cacheKey, price.toString(), Duration.ofMinutes(10));
                    log.info("✅ API 成功取得價格，寫入 Redis: {} -> {}", cacheKey, price);
                })
                .doOnError(e -> log.error("❌ 呼叫 product-service 發生錯誤：{}", e.getMessage()));
    }

    /**
     * Resilience4j fallback 方法（要完全匹配原本方法參數 + Throwable）
     */
    private Mono<BigDecimal> fallbackPrice(String productCode, String cacheKey, Throwable e) {
        log.warn("🛟 CircuitBreaker Fallback（productApi）：{} → 使用預設價格", e.getMessage());
        return Mono.just(BigDecimal.valueOf(999));
    }

    private record ProductResponse(String productCode, String name, BigDecimal price) {}
}