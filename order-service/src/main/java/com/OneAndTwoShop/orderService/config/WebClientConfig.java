package com.OneAndTwoShop.orderService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${oneshop.user-service.base-url}")
    private String userServiceBaseUrl;

    @Bean
    public WebClient userClient(WebClient.Builder builder) {
        return builder.baseUrl(userServiceBaseUrl).build();
    }
}