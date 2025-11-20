package com.OneAndTwoShop.userService.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * i18n 設定：
 * - 設定 messages 檔案位置
 * - 設定預設語系（這裡先用繁中）
 * - 之後可以用 Accept-Language 切換語系
 */
@Configuration
public class I18nConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        // 尋找 src/main/resources/messages*.properties
        ms.setBasename("classpath:messages");
        ms.setDefaultEncoding("UTF-8");
        // 如果找不到 key，就用 key 本身
        ms.setUseCodeAsDefaultMessage(true);
        return ms;
    }

    /**
     * 用 Accept-Language 決定語系。
     * 如果沒有帶，就用繁體中文。
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.TRADITIONAL_CHINESE);
        return resolver;
    }
}
