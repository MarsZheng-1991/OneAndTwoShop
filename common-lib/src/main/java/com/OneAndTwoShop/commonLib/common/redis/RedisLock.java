package com.OneAndTwoShop.commonLib.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisLock {

    private final StringRedisTemplate redis;

    public String tryLock(String key, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent(key, token, ttl);
        return (ok != null && ok) ? token : null;
    }

    public boolean unlock(String key, String token) {
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "  return redis.call('del', KEYS[1]) " +
                        "else return 0 end";
        Long res = redis.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key), token);
        return res != null && res > 0;
    }
}