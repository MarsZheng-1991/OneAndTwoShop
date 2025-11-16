package com.OneAndTwoShop.productService.service;

import com.OneAndTwoShop.productService.dto.ProductResponse;
import com.OneAndTwoShop.productService.model.Product;
import com.OneAndTwoShop.productService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final StringRedisTemplate redis;

    private static final String PRODUCT_CACHE_PREFIX = "product:";

    public ProductResponse getProductByCode(String code) {
        String cacheKey = PRODUCT_CACHE_PREFIX + code;

        // 1️⃣ 查快取
        String cached = redis.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("🎯 Redis cache hit for product {}", code);
            return ProductResponse.fromCacheString(cached);
        }

        // 2️⃣ 查 DB
        Product product = productRepository.findByProductCode(code)
                .orElseThrow(() -> new RuntimeException("product.not.found"));

        // 3️⃣ 寫入快取
        String cacheValue = product.getName() + "|" + product.getPrice();
        redis.opsForValue().set(cacheKey, cacheValue, Duration.ofMinutes(10));

        log.info("💾 Redis cache miss → load product {} into cache", code);

        return new ProductResponse(product.getProductCode(), product.getName(), product.getPrice());
    }
}
