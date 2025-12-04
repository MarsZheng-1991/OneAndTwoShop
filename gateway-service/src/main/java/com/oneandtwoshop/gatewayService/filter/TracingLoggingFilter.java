package com.oneandtwoshop.gatewayService.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class TracingLoggingFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String traceId = Optional.ofNullable(request.getHeaders().getFirst(TRACE_ID_HEADER))
                .orElse(UUID.randomUUID().toString());

        // 把 traceId 往下游帶
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(TRACE_ID_HEADER, traceId)
                .build();

        mutatedRequest.getMethod();
        String method = mutatedRequest.getMethod().name();
        String path = mutatedRequest.getURI().getPath();

        String clientIp = Optional.ofNullable(request.getRemoteAddress())
                .map(addr -> addr.getAddress().getHostAddress())
                .orElse("UNKNOWN");

        long start = System.currentTimeMillis();

        log.info("[GW] ➡️ [{}] {} {} from ip={}", traceId, method, path, clientIp);

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doOnError(ex -> log.error("[GW] ❌ [{}] {} {} error: {}",
                        traceId, method, path, ex.getMessage(), ex))
                .then(Mono.fromRunnable(() -> {
                    long cost = System.currentTimeMillis() - start;
                    HttpStatusCode status = exchange.getResponse().getStatusCode();
                    int statusCode = status != null ? status.value() : 0;
                    log.info("[GW] ⬅️ [{}] {} {} -> status={} cost={}ms",
                            traceId, method, path, statusCode, cost);
                }));
    }

    @Override
    public int getOrder() {
        return -100; // 越小越早執行
    }
}