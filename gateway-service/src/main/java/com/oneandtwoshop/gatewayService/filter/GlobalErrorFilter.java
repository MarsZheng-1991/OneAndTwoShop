package com.oneandtwoshop.gatewayService.filter;

import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.commonLib.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalErrorFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange)
                .onErrorResume(ex -> {

                    log.error("[GW] 💥 Downstream error: {}", ex.getMessage(), ex);

                    ApiResponse<ApiData<Object>> body =
                            new ApiResponse<>(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    new ApiData<>("gateway.downstream.error", null)
                            );

                    byte[] bytes;
                    try {
                        bytes = objectMapper.writeValueAsBytes(body);
                    } catch (JsonProcessingException e) {
                        String fallbackJson = """
                                {"code":500,"data":{"message":"gateway.json.error","data":null}}
                                """;
                        bytes = fallbackJson.getBytes(StandardCharsets.UTF_8);
                    }

                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    DataBuffer buffer = exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes);

                    return exchange.getResponse().writeWith(Mono.just(buffer));
                });
    }

    @Override
    public int getOrder() {
        return -50; // 在 TracingLoggingFilter（-100）之後
    }
}