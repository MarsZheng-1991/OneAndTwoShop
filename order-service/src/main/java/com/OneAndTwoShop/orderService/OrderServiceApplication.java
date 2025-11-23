package com.OneAndTwoShop.orderService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "com.OneAndTwoShop.orderService",
                "com.OneAndTwoShop.commonLib"
        }
)
@EnableJpaRepositories(basePackages = {
        "com.OneAndTwoShop.orderService.repository",
        "com.OneAndTwoShop.commonLib.common.i18n"
})
@EntityScan(basePackages = {
        "com.OneAndTwoShop.orderService.model",
        "com.OneAndTwoShop.commonLib.common.i18n"
})
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}