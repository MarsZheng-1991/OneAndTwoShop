package com.OneAndTwoShop.productService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.OneAndTwoShop.productService",
        "com.OneAndTwoShop.commonLib"
})
@EnableJpaRepositories(basePackages = {
        "com.OneAndTwoShop.productService.repository",
        "com.OneAndTwoShop.commonLib.common.i18n"
})
@EntityScan(basePackages = {
        "com.OneAndTwoShop.productService.model",
        "com.OneAndTwoShop.commonLib.common.i18n"
})
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}