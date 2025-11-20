package com.OneAndTwoShop.commonLib.common.i18n;

import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class TranslateService {

    private final ErrorMessageRepository repository;

    public TranslateService(ErrorMessageRepository repository) {
        this.repository = repository;
    }

    public String translate(String key, Locale locale) {

        return repository.findById(key)
                .map(msg -> {
                    String lang = locale.getLanguage().toLowerCase();

                    return switch (lang) {
                        case "en" -> msg.getEn();
                        case "zh" -> msg.getZh();
                        default -> msg.getZh();
                    };
                })
                .orElse("未知錯誤：" + key);
    }
}
