package com.OneAndTwoShop.commonLib.common.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorMessageService {

    private final ErrorMessageRepository repository;

    public String translate(String key, String locale) {
        return repository.findById(key)
                .map(entity -> {
                    if ("zh".equalsIgnoreCase(locale) || "zh-TW".equalsIgnoreCase(locale)) {
                        return entity.getZh();
                    } else {
                        return entity.getEn();
                    }
                })
                .orElse(key); // 找不到就直接回 key，避免炸錯
    }
}