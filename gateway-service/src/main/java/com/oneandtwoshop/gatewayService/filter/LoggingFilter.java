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

import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        // 產生 requestId（用於整個鏈路追蹤）
        String requestId = UUID.randomUUID().toString();
        exchange.getAttributes().put("requestId", requestId);

        // Request 基本資訊
        request.getMethod();
        String method = request.getMethod().name();
        String path = request.getURI().getRawPath();
        String query = request.getURI().getRawQuery();
        String fullPath = (query != null) ? path + "?" + query : path;

        // Client IP（包含 proxy / load balancer）
        String clientIp =
                request.getHeaders().getFirst("X-Forwarded-For") != null ?
                        request.getHeaders().getFirst("X-Forwarded-For") :
                        request.getRemoteAddress() != null ?
                                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";

        long startTime = System.currentTimeMillis();

        log.info("""
                🌐 Incoming Request
                { "id": "%s", "ip": "%s", "method": "%s", "path": "%s" }
                """.formatted(requestId, clientIp, method, fullPath));

        return chain.filter(exchange)
                .doOnError(ex -> {
                    log.error("""
                            ❌ Error Occurred
                            { "id": "%s", "method": "%s", "path": "%s", "error": "%s" }
                            """.formatted(requestId, method, fullPath, ex.getMessage()), ex);
                })
                .then(Mono.fromRunnable(() -> {

                    long cost = System.currentTimeMillis() - startTime;

                    HttpStatusCode status = exchange.getResponse().getStatusCode();
                    int statusCode = (status != null) ? status.value() : 0;

                    String contentType = exchange.getResponse()
                            .getHeaders()
                            .getFirst("Content-Type");

                    log.info("""
                            📤 Response Sent
                            { "id": "%s", "method": "%s", "path": "%s",
                              "status": %d, "contentType": "%s", "costMs": %d }
                            """.formatted(
                            requestId,
                            method,
                            fullPath,
                            statusCode,
                            contentType != null ? contentType : "unknown",
                            cost
                    ));
                }));
    }

    @Override
    public int getOrder() {
        return -1; // 越小越早執行
    }
}