package com.OneAndTwoShop.orderService.service;

import reactor.core.publisher.Mono;

public interface UserVerificationService {
    Mono<Boolean> verifyUserExists(String userId);
}
