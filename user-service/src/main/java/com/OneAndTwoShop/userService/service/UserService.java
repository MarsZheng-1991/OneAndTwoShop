package com.OneAndTwoShop.userService.service;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.userService.model.User;
import com.OneAndTwoShop.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // 取得所有使用者
    public Mono<ApiData<List<User>>> getAllUsers() {
        return Mono.fromCallable(userRepository::findAll)
                .map(ApiData::success)
                .onErrorResume(this::handleError);
    }

    // 查詢 ID
    public Mono<ApiData<User>> getUserById(Long id) {
        return Mono.fromCallable(() ->
                        userRepository.findById(id)
                                .orElseThrow(() -> new BusinessException("user.notfound"))
                ).map(ApiData::success)
                .onErrorResume(this::handleError);
    }

    // 建立使用者
    public Mono<ApiData<String>> createUser(User user) {
        return Mono.fromCallable(() -> {
            userRepository.save(user);
            return ApiData.successMessage("system.success");
        }).onErrorResume(this::handleError);
    }

    // 更新使用者
    public Mono<ApiData<String>> updateUser(Long id, User user) {
        return Mono.fromCallable(() -> {

            User existing = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("user.notfound"));

            existing.setUsername(user.getUsername());
            existing.setEmail(user.getEmail());
            existing.setAddress(user.getAddress());
            existing.setBirthday(user.getBirthday());
            existing.setChildBirthday(user.getChildBirthday());
            existing.setGender(user.getGender());

            userRepository.save(existing);

            return ApiData.successMessage("system.success");

        }).onErrorResume(this::handleError);
    }

    // 刪除使用者
    public Mono<ApiData<String>> deleteUser(Long id) {
        return Mono.fromCallable(() -> {

            if (!userRepository.existsById(id)) {
                throw new BusinessException("user.notfound");
            }

            userRepository.deleteById(id);
            return ApiData.successMessage("system.success");

        }).onErrorResume(this::handleError);
    }

    // -----------------------------------------------------------
    // 統一錯誤處理
    // -----------------------------------------------------------
    private <T> Mono<T> handleError(Throwable ex) {

        if (ex instanceof BusinessException) {
            return Mono.error(ex);
        }

        log.error("[UserService Unknown Error]", ex);

        return Mono.error(new BusinessException("system.error"));
    }
}