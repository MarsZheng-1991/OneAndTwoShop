package com.OneAndTwoShop.userService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.OneAndTwoShop")
@EnableJpaRepositories(basePackages = {
        "com.OneAndTwoShop.userService.repository",
        "com.OneAndTwoShop.commonLib.common.i18n"
})
@EntityScan(basePackages = {
        "com.OneAndTwoShop.userService.model",
        "com.OneAndTwoShop.commonLib.common.i18n"
})
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
